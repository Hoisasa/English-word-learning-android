package com.sharksempire.englishcards.viewmodels

import androidx.lifecycle.viewModelScope
import com.sharksempire.englishcards.repositories.DictionaryRepository
import com.sharksempire.englishcards.ui.composables.screens.LessonViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


@HiltViewModel
class LessonViewModel @Inject constructor(repo: DictionaryRepository): AbstractLessonViewModel(repo) {
    
    override fun handleAnswer(isCorrect: Boolean) = viewModelScope.launch {
        val currentState = _internalStorageFlow.value as LessonViewState.Success
        val newMistakes = if (isCorrect) currentState.mistakes
        else currentState.mistakes + currentState.words[currentState.currentIndex]
        repo.updateWeight(
            wordId = currentState.words[currentState.currentIndex].id,
            mode = currentState.mode,
            isCorrect,
            MAX_POINTS,
        )
        
        _internalStorageFlow.update {
            return@update currentState.copy(
                currentIndex = getNextIndex(),
                isTranslationPressed = false,
                mistakes = newMistakes
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
    
    override fun prepareLesson() = viewModelScope.launch {
        val currentState = _internalStorageFlow.value as LessonViewState.Success
        
        val weightsUpdate = repo.getNewWeights(currentState.subGroup!!)
        val newWordsList = generateNewWordsList()
        _internalStorageFlow.update { return@update LessonViewState.Loading }
        
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
            return@update LessonViewState.Success(
                isInitComplete = true,
                subGroup = currentState.subGroup,
                rawWords = currentState.rawWords,
                words = newWordsList,
            )
        }
    }
}