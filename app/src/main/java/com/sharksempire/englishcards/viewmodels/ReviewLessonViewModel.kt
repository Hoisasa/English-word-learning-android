package com.sharksempire.englishcards.viewmodels
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.sharksempire.englishcards.repositories.DictionaryRepository
import com.sharksempire.englishcards.ui.composables.Screen
import com.sharksempire.englishcards.ui.composables.screens.LessonViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


@HiltViewModel
class ReviewLessonViewModel @Inject constructor(
    repo: DictionaryRepository,
    savedStateHandle: SavedStateHandle
): AbstractLessonViewModel(repo) {
    private val args = Screen.ReviewLesson.from(savedStateHandle)
    
    init {
        getReviewWords(args.levelTarget, args.subgroupTarget)
    }
    
    fun getReviewWords(levelTarget: Int, subgroupTarget: String?) = viewModelScope.launch {
        _internalStorageFlow.update { return@update LessonViewState.Loading }
        repo.getReviewWords(levelTarget, subgroupTarget).onSuccess { words ->
            _internalStorageFlow.update {
                return@update LessonViewState.Success(
                    isInitComplete = true,
                    subGroup = null,
                    rawWords = words,
                    words = words,
                    mode = LessonViewState.Success.StudyMode.REVW
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
    
    override fun handleAnswer(isCorrect: Boolean) = viewModelScope.launch {
        val currentState = _internalStorageFlow.value as LessonViewState.Success
        val newMistakes = if (isCorrect) currentState.mistakes
            else currentState.mistakes + currentState.words[currentState.currentIndex]
        
        when(currentState.mode) {
            is LessonViewState.Success.StudyMode.REVW -> {
                repo.updateLevel(
                    wordId = currentState.words[currentState.currentIndex].id,
                    isCorrect
                )
            }
            LessonViewState.Success.StudyMode.OVER,
                LessonViewState.Success.StudyMode.PRAC,
                LessonViewState.Success.StudyMode.EXAM-> {}
        }
        
        _internalStorageFlow.update {
            return@update currentState.copy(
                currentIndex = getNextIndex(),
                isTranslationPressed = false,
                mistakes = newMistakes
            )
        }
    }
    
    override fun prepareLesson(): Job {
        TODO("Not yet implemented")
    }
}