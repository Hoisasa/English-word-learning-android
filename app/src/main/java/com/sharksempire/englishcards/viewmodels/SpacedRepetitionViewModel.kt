package com.sharksempire.englishcards.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sharksempire.englishcards.repositories.DictionaryRepository
import com.sharksempire.englishcards.ui.composables.screens.ReviewViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class SpacedRepetitionViewModel @Inject constructor(
    private val repo: DictionaryRepository
): ViewModel() {
    private val _internalStorageFlow = MutableStateFlow<ReviewViewState>(
        value = ReviewViewState.Loading
    )
    
    val uiState = _internalStorageFlow.asStateFlow()
    
    
    init {
        getLevels()
    }
    
    fun getLevels() = viewModelScope.launch {
        _internalStorageFlow.update { return@update ReviewViewState.Loading }
        repo.getSpacedRepetitionLevels().onSuccess { levels ->
            _internalStorageFlow.update {
                return@update ReviewViewState.Success(
                    contentGeneral = levels,
                    contentGrouped = null
                )
            }
        }.onFailure { exception ->
            _internalStorageFlow.update {
                return@update ReviewViewState.Error(
                    message = exception.message ?: "Unknown error occurred"
                )
            }
        }
    }
}