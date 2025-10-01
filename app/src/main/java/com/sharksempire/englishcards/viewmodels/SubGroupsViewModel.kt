package com.sharksempire.englishcards.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sharksempire.englishcards.repositories.DictionaryRepository
import com.sharksempire.englishcards.ui.composables.GroupsViewState
import com.sharksempire.englishcards.ui.composables.Screen
import com.sharksempire.englishcards.ui.composables.screens.SubGroupsViewState
import dagger.assisted.Assisted
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubGroupsViewModel @Inject constructor(
    private val repo: DictionaryRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val args = Screen.SubGroups.from(savedStateHandle)
    
    private val _internalStorageFlow = MutableStateFlow<SubGroupsViewState>(
        value = SubGroupsViewState.Loading
    )
    
    val uiState = _internalStorageFlow.asStateFlow()
    
    init {
        getSubgroups(args.target)
    }

    fun getSubgroups(target: String) = viewModelScope.launch{
        _internalStorageFlow.update { return@update SubGroupsViewState.Loading }
        repo.getSubgroups(target).onSuccess { groups ->
            _internalStorageFlow.update {
                return@update SubGroupsViewState.Success(
                    content = groups
                )
            }
        }.onFailure { exception ->
            _internalStorageFlow.update {
                return@update SubGroupsViewState.Error(
                    message = exception.message ?: "Unknown error occurred"
                )
            }
        }
    }
}
