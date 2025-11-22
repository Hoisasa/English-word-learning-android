package com.sharksempire.englishcards.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sharksempire.englishcards.dao.WordData
import com.sharksempire.englishcards.repositories.DictionaryRepository
import com.sharksempire.englishcards.ui.composables.screens.LessonViewState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

abstract class AbstractLessonViewModel(
    protected val repo: DictionaryRepository
) : ViewModel() {
    val MAX_POINTS = 5
    protected val _internalStorageFlow = MutableStateFlow<LessonViewState>(
        value = LessonViewState.Loading
    )
    
    val uiState = _internalStorageFlow.asStateFlow()
    
    abstract fun handleAnswer(isCorrect: Boolean): Job
    abstract fun prepareLesson(): Job
    
    fun getCurrentWord(): WordData {
        val currentState = _internalStorageFlow.value as LessonViewState.Success
        return currentState.words[currentState.currentIndex]
    }
    fun setMode(mode: LessonViewState.Success.StudyMode) = viewModelScope.launch {
        val current = _internalStorageFlow.value as? LessonViewState.Success ?: return@launch
        if (current.mode.displayName == mode.displayName) {
            return@launch
        } else {
            _internalStorageFlow.update {
                return@update current.copy(
                    mode = mode
                )
            }
            prepareLesson()
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
    
    fun getNextIndex(): Int {
        val currentState = _internalStorageFlow.value as LessonViewState.Success
        val isEnd = currentState.currentIndex == currentState.words.size -1
        val newIndex = if (isEnd) 0 else currentState.currentIndex +1
        
        return newIndex
    }
    
    fun setNextIndex() = viewModelScope.launch {
        val currentState = _internalStorageFlow.value as LessonViewState.Success
        _internalStorageFlow.update {
            return@update currentState.copy(
                currentIndex = getNextIndex()
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
    
    fun markExamCompleted() = viewModelScope.launch {
        val currentState = _internalStorageFlow.value as LessonViewState.Success
        repo.setExamCompleted(currentState.subGroup!!)
    }
    
    fun getScore(): Int {
        val currentState = _internalStorageFlow.value as LessonViewState.Success
        val correctAnswers = currentState.words.size - currentState.mistakes.size
        val lessonScore = ((correctAnswers.toFloat() / currentState.words.size) * 100).roundToInt()
        return lessonScore
    }
}