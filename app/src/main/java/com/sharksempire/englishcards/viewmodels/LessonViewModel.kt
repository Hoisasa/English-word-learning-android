package com.sharksempire.englishcards.viewmodels

import androidx.compose.foundation.layout.Box
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sharksempire.englishcards.dao.WordData
import com.sharksempire.englishcards.repositories.DictionaryRepository
import com.sharksempire.englishcards.ui.composables.screens.LessonViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


@HiltViewModel
class LessonViewModel @Inject constructor(private val repo: DictionaryRepository): ViewModel() {
    val MAX_POINTS = 5
    private val _internalStorageFlow = MutableStateFlow<LessonViewState>(
        value = LessonViewState.Loading
    )
    
    val uiState = _internalStorageFlow.asStateFlow()
    
    fun getWords(target: String) = viewModelScope.launch {
        _internalStorageFlow.update { return@update LessonViewState.Loading }
        repo.getWords(target).onSuccess { words ->
            _internalStorageFlow.update {
                return@update LessonViewState.Success(
                    subGroup = target,
                    rawWords = words,
                    words = words,
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
    
    fun getCurrentWord(): WordData {
        val currentState = _internalStorageFlow.value as LessonViewState.Success
        return currentState.words[currentState.currentIndex]
    }
    fun setMode(mode: LessonViewState.Success.StudyMode) = viewModelScope.launch {
        val current = _internalStorageFlow.value as? LessonViewState.Success ?: return@launch
        _internalStorageFlow.update {
            return@update current.copy(
                mode = mode
            )
        }
    }
    
    fun translate() = viewModelScope.launch {
        val currentState = _internalStorageFlow.value as LessonViewState.Success
        _internalStorageFlow.update {
            return@update currentState.copy(
                isTranslationPressed = true
            )
        }
    }
    
    fun resetIndex() = viewModelScope.launch {
        val currentState = _internalStorageFlow.value as LessonViewState.Success
        _internalStorageFlow.update {
            return@update currentState.copy(
                currentIndex = 0
            )
        }
    }
    
    fun handleAnswer(isCorrect: Boolean, isEnd: Boolean) = viewModelScope.launch {
        val currentState = _internalStorageFlow.value as LessonViewState.Success
        val newIndex = if (isEnd) 0 else currentState.currentIndex +1
        val newMistakes = if (isCorrect) currentState.mistakes
            else currentState.mistakes + currentState.words[currentState.currentIndex]
        
        var mark = if (isCorrect) 1f else -1f
        mark = mark / MAX_POINTS
        when(currentState.mode) {
            is LessonViewState.Success.StudyMode.PRAC ->
                repo.updateWeight(
                    wordId = currentState.words[currentState.currentIndex].id,
                    mark = mark
                )
            is LessonViewState.Success.StudyMode.EXAM ->
                repo.updateWeight(
                    wordId = currentState.words[currentState.currentIndex].id,
                    mark = 3 * mark
                )
            is LessonViewState.Success.StudyMode.UNKN ->
                _internalStorageFlow.update {
                    return@update LessonViewState.Error("There was an issue setting study mode")
                }
            LessonViewState.Success.StudyMode.OVER -> {}
        }
        
        _internalStorageFlow.update {
            return@update currentState.copy(
                currentIndex = newIndex,
                isTranslationPressed = false,
                mistakes = newMistakes
            )
        }
    }
    
    fun prepareLesson() = viewModelScope.launch {
        val currentState = _internalStorageFlow.value as LessonViewState.Success
        val weightsUpdate = repo.getNewWeights(currentState.subGroup)
        val newWordsList = generateNewWordsList()
        
        weightsUpdate.onSuccess { map ->
            newWordsList.forEach { word ->
                word.weight = map.getValue(word.id)
                
            }
        }.onFailure { exception ->
            _internalStorageFlow.update {
                return@update LessonViewState.Error(
                    message = exception.message ?: "Unknown error occurred"
                )
            }
        }
        
        _internalStorageFlow.update {
            return@update currentState.copy(
                words = newWordsList,
                mistakes = emptyList(),
                currentIndex = 0,
                isTranslationPressed = false,
            )
        }
    }
    
    fun generateNewWordsList(): List<WordData> {
        val currentState = _internalStorageFlow.value as LessonViewState.Success
        var newWordsList = currentState.rawWords
        val learned: List<WordData>
        var to_learn: List<WordData>
        
        when (currentState.mode) {
            LessonViewState.Success.StudyMode.PRAC -> {
                to_learn = newWordsList.filter { it.weight < 1f }
                to_learn = to_learn.shuffled()
                learned = newWordsList.filter { it.weight == 1f }
                if (learned.isNotEmpty()) {
                    newWordsList = to_learn + learned.random()
                } else {
                    newWordsList = to_learn
                }
            }
            LessonViewState.Success.StudyMode.EXAM ->
                newWordsList = newWordsList.shuffled()
            else -> {}
        }
        return newWordsList
    }
    
    fun getScore(): Int {
        val currentState = _internalStorageFlow.value as LessonViewState.Success
        val correctAnswers = currentState.words.size - currentState.mistakes.size
        val lessonScore = ((correctAnswers.toFloat() / currentState.words.size) * 100).roundToInt()
        return lessonScore
    }
}