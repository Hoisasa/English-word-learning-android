package com.sharksempire.englishcards.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sharksempire.englishcards.repositories.DictionaryRepository
import com.sharksempire.englishcards.ui.composables.GroupsViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroupsViewModel @Inject constructor(private val repo: DictionaryRepository) :
    ViewModel() {
    
    private val _internalStorageFlow = MutableStateFlow<GroupsViewState>(
        value = GroupsViewState.Loading
    )
    
    val uiState = _internalStorageFlow.asStateFlow()
    
    init {
        getGroups()
    }
    
    fun getGroups() = viewModelScope.launch{
        _internalStorageFlow.update { return@update GroupsViewState.Loading }
        repo.getGroups().onSuccess { groups ->
            _internalStorageFlow.update {
                return@update GroupsViewState.Success(
                    filterState = GroupsViewState.Success.FilterState(
                        groups.map {it.pos}.toSet().toList(),
                        groups.map {it.pos}.toSet().toList()
                    ),
                    content = groups,
                )
            }
        }.onFailure { exception ->
            _internalStorageFlow.update {
                return@update GroupsViewState.Error(
                    message = exception.message ?: "Unknown error occurred"
                )
            }
        }
    }
    
    fun toggleFilter(filter: String) = viewModelScope.launch{
        _internalStorageFlow.update {
            val current = it as GroupsViewState.Success
            val all = current.filterState.allFilters
            val selected = current.filterState.selectedFilters
            
            val newValues = if (selected.toSet() == all.toSet()) {
                listOf(filter)
            } else if (filter in selected) {
                if (listOf(filter) == selected) {
                    all
                } else {
                    selected - filter
                }
            } else {
                selected + filter
            }
            return@update current.copy(
                filterState = current.filterState.copy(selectedFilters = newValues)
            )
        }
    }
}
