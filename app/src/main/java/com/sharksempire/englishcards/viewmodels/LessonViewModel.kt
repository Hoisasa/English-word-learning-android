package com.sharksempire.englishcards.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.sharksempire.englishcards.dao.WordData
import com.sharksempire.englishcards.repositories.DictionaryRepository
import com.sharksempire.englishcards.ui.composables.Screen
import com.sharksempire.englishcards.ui.composables.screens.LessonViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


@HiltViewModel
class LessonViewModel @Inject constructor(
    repo: DictionaryRepository,
    savedStateHandle: SavedStateHandle
): AbstractLessonViewModel(repo) {
    private val args = Screen.SubGroups.from(savedStateHandle)
    
    init {
        getWords(args.target)
    }
    
    
    override fun handleAnswer(isCorrect: Boolean) = viewModelScope.launch {
        val currentState = _internalStorageFlow.value as LessonViewState.Success
        val data = currentState.lessonData as LessonViewState.LessonData.Ready
        val newMistakes = if (isCorrect) data.mistakes
        else data.mistakes + data.words[data.currentIndex]
        repo.updateWeight(
            wordId = data.words[data.currentIndex].id,
            mode = data.mode,
            isCorrect,
            MAX_POINTS,
        )
        
        _internalStorageFlow.update {
            return@update currentState.copy(
                lessonData = data.copy(
                    currentIndex = getNextIndex(),
                    isTranslationPressed = false,
                    mistakes = newMistakes
                ),
            )
        }
    }
    
    fun getWords(target: String) = viewModelScope.launch {
        _internalStorageFlow.update { return@update LessonViewState.Loading }
        repo.getWords(target).onSuccess { words ->
            _internalStorageFlow.update {
                return@update LessonViewState.Success(
                    subGroup = target,
                    rawWords = words,
                    lessonData = LessonViewState.LessonData.Loading
                )
            }
        }.onFailure { exception ->
            _internalStorageFlow.update {
                return@update LessonViewState.Error(
                    message = exception.message ?: "Unknown error occurred"
                )
            }
        }
    }
    
    fun prepareLesson(modeStr: String) = viewModelScope.launch {
        val currentState = _internalStorageFlow.value as LessonViewState.Success

        val mode = resolveModeFromString(modeStr)
        val newWords = generateNewWordsList(mode)
        
        _internalStorageFlow.update {
            return@update currentState.copy(
                lessonData = LessonViewState.LessonData.Ready(
                    words = newWords,
                    mode = mode,
                )
            )
        }
    }
    
    override fun restartLesson() = viewModelScope.launch {
        val currentState = _internalStorageFlow.value as LessonViewState.Success
        val data = currentState.lessonData as LessonViewState.LessonData.Ready
        
        _internalStorageFlow.update { return@update currentState.copy(
                lessonData = LessonViewState.LessonData.Loading
            )
        }
        
        val weightsUpdate = repo.getNewWeights(currentState.subGroup)
        val newWordsList = generateNewWordsList(data.mode)
        
        weightsUpdate.onSuccess { map ->
            newWordsList.forEach { word ->
                word.weight = map.getValue(word.id)
            }
        }.onFailure { exception ->
            _internalStorageFlow.update {
                return@update LessonViewState.Error(
                    message = exception.message ?: "Could not update weights properly"
                )
            }
        }
        
        _internalStorageFlow.update {
            return@update LessonViewState.Success(
                subGroup = currentState.subGroup,
                rawWords = currentState.rawWords,
                lessonData = LessonViewState.LessonData.Ready(
                    words = newWordsList,
                    mode = data.mode
                )
            )
        }
    }
    
    fun generateNewWordsList(mode: LessonViewState.Success.StudyMode): List<WordData> {
        val currentState = _internalStorageFlow.value as LessonViewState.Success
        var newWordsList = currentState.rawWords
        
        val learned: List<WordData>
        var to_learn: List<WordData>
        
        when (mode) {
            LessonViewState.Success.StudyMode.PRAC -> {
                to_learn = newWordsList.filter { it.weight < 1f }
                to_learn = to_learn.shuffled()
                learned = newWordsList.filter { it.weight == 1f }
                newWordsList = if (learned.isNotEmpty()) {
                    to_learn + learned.random()
                } else {
                    to_learn
                }
            }
            LessonViewState.Success.StudyMode.EXAM ->
                newWordsList = newWordsList.shuffled()
            else -> {}
        }
        return newWordsList
    }
}

