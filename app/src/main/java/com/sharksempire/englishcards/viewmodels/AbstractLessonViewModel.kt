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
    abstract fun restartLesson(): Job
    
    fun resolveModeFromString(modeStr: String): LessonViewState.Success.StudyMode {
        val modesList = LessonViewState.Success.StudyMode::class.sealedSubclasses.mapNotNull { it.objectInstance }
        val modesMap = modesList.associateBy { it.displayName }
        val mode = modesMap[modeStr]!!
        return mode
    }
    
    fun getCurrentWord(): WordData {
        val currentState = _internalStorageFlow.value as LessonViewState.Success
        val data = currentState.lessonData as LessonViewState.LessonData.Ready
        return data.words[data.currentIndex]
    }
    
    fun translate() = viewModelScope.launch {
        val currentState = _internalStorageFlow.value as LessonViewState.Success
        val data = currentState.lessonData as LessonViewState.LessonData.Ready
        _internalStorageFlow.update {
            return@update currentState.copy(
                lessonData = data.copy(
                    isTranslationPressed = true
                )
            )
        }
    }
    
    fun resetIndex() = viewModelScope.launch {
        val currentState = _internalStorageFlow.value as LessonViewState.Success
        val data = currentState.lessonData as LessonViewState.LessonData.Ready
        _internalStorageFlow.update {
            return@update currentState.copy(
                lessonData = data.copy(
                    currentIndex = 0
                )
            )
        }
    }
    
    fun getNextIndex(): Int {
        val currentState = _internalStorageFlow.value as LessonViewState.Success
        val data = currentState.lessonData as LessonViewState.LessonData.Ready
        val isEnd = data.currentIndex == data.words.size -1
        val newIndex = if (isEnd) 0 else data.currentIndex +1
        
        return newIndex
    }
    
    fun setNextIndex() = viewModelScope.launch {
        val currentState = _internalStorageFlow.value as LessonViewState.Success
        val data = currentState.lessonData as LessonViewState.LessonData.Ready
        _internalStorageFlow.update {
            return@update currentState.copy(
                lessonData = data.copy(
                    currentIndex = getNextIndex()
                )
            )
        }
    }
    
    
    
    
    fun markExamCompleted() = viewModelScope.launch {
        val currentState = _internalStorageFlow.value as LessonViewState.Success
        repo.setExamCompleted(currentState.subGroup)
    }
    
    fun getScore(): Int {
        val currentState = _internalStorageFlow.value as LessonViewState.Success
        val data = currentState.lessonData as? LessonViewState.LessonData.Ready ?: return 0
        val correctAnswers = data.words.size - data.mistakes.size
        val lessonScore = ((correctAnswers.toFloat() / data.words.size) * 100).roundToInt()
        return lessonScore
    }
}