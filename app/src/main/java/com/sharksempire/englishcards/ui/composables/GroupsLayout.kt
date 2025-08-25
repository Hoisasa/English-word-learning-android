package com.sharksempire.englishcards.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import com.sharksempire.englishcards.components.groups.GroupButtonWithProgress
import com.sharksempire.englishcards.components.groups.GroupsFilterItem
import com.sharksempire.englishcards.ui.theme.MyGreen
import com.sharksempire.englishcards.ui.theme.MyGreenText
import com.sharksempire.englishcards.ui.theme.MyPurple
import com.sharksempire.englishcards.ui.theme.MyPurpleShadow
import com.sharksempire.englishcards.viewmodels.MainActivityViewModel

sealed interface GroupsViewState {
    object Loading : GroupsViewState
    data class Error(val message: String) : GroupsViewState
    data class Success(
        val filterState: FilterState,
        val content: List<Item>
    ) : GroupsViewState {
        data class FilterState (
            val allFilters: List<String>,
            val selectedFilters: List<String>,
        )
    }
}


@Composable
fun Display_groups(
    onGroupSelected: (String) -> Unit,
    onReviewSelected: () -> Unit = {},
    viewModel: MainActivityViewModel = hiltViewModel(),
) {
    LaunchedEffect(Unit, block = { viewModel.getGroups() })
    val state by viewModel.uiState.collectAsState()
    
    when(val viewState = state) {
        GroupsViewState.Loading -> CircularProgressIndicator(modifier = Modifier.size(50.dp))
        is GroupsViewState.Error -> Text(text = viewState.message)
        is GroupsViewState.Success -> {
            ConstraintLayout(
                modifier = Modifier.fillMaxSize()
            ) {
                val (buttons, filters, guidelineLine) = createRefs()
                val filtersGuideline = createGuidelineFromTop(0.9f)
                
                // A Box that represents your painted guideline
                Box(
                    modifier = Modifier
                        .constrainAs(guidelineLine) {
                            top.linkTo(filtersGuideline)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color.Red) // You can change the color here
                )
                
                GroupsFilterItem(
                    filters = viewState.filterState,
                    onFilterCLicked = viewModel::toggleFilter,
                    modifier = Modifier
                    .constrainAs(filters) {
                        top.linkTo(filtersGuideline)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        width = Dimension.fillToConstraints
                    }
                )

                LazyColumn(
                    modifier = Modifier
                        .constrainAs(buttons) {
                            top.linkTo(parent.top)
                            bottom.linkTo(filtersGuideline)
                            height = Dimension.fillToConstraints
                        },
                    verticalArrangement = Arrangement.Center,
                ) {
                    item {

                        GroupButtonWithProgress(
                            onClick = { onReviewSelected },
                            group = Item.GroupsWithProgressData(
                                name = "Интервальное повторение",
                                learned = 0,
                                total = 1,
                                pos = "noneclass"
                            ),
                            colors = listOf(MyGreen, MyPurpleShadow, MyGreen, MyGreenText),
                        )
                    }
                    
                    val groups = viewState.content.filterIsInstance<Item.GroupsWithProgressData>()
                    items(
                        items = groups.filter { it.pos in viewState.filterState.selectedFilters },
                        key = { it.name }
                    ) { group ->
                        GroupButtonWithProgress(
                            onClick = onGroupSelected,
                            group,
                            colors = listOf(MyPurple, MyGreenText, MyGreen, MyPurpleShadow),
                            modifier = Modifier.animateItem()
                        )
                    }
                }
            }
        }
    }
}
