package com.sharksempire.englishcards.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import com.sharksempire.englishcards.components.groups.GroupButtonWithProgress
import com.sharksempire.englishcards.components.groups.GroupsFilterItem
import com.sharksempire.englishcards.ui.theme.MyGreen
import com.sharksempire.englishcards.ui.theme.MyGreenText
import com.sharksempire.englishcards.ui.theme.MyPurple
import com.sharksempire.englishcards.ui.theme.MyPurpleShadow
import com.sharksempire.englishcards.viewmodels.GroupsViewModel

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
    viewModel: GroupsViewModel = hiltViewModel(),
) {

    val state by viewModel.uiState.collectAsState()

    when(val viewState = state) {
        GroupsViewState.Loading -> CircularProgressIndicator(modifier = Modifier.size(50.dp))
        is GroupsViewState.Error -> {
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = viewState.message, fontSize = 20.sp)
            }
        }

        is GroupsViewState.Success -> {
            val buttonsId = "buttons"
            val filtersId = "filters"

            val startSet = ConstraintSet {
                val (buttons, filters) = createRefsFor(buttonsId, filtersId)
                val filtersGuideline = createGuidelineFromTop(0.9f)

                constrain(buttons) {
                    top.linkTo(parent.top)
                    bottom.linkTo(filtersGuideline)
                    height = Dimension.fillToConstraints
                }

                constrain(filters) {
                    top.linkTo(filtersGuideline)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                }
            }

//            val endSet = ConstraintSet {
//                val (buttons, filters) = createRefsFor(buttonsId, filtersId)
//                val filtersGuideline = createGuidelineFromTop(0.9f)
//
//                constrain(buttons) {
//                    top.linkTo(parent.top)
//                    bottom.linkTo(filtersGuideline)
//                    height = Dimension.fillToConstraints
//                }
//
//                constrain(filters) {
//                    top.linkTo(filtersGuideline)
//                    bottom.linkTo(parent.bottom)
//                    start.linkTo(parent.start)
//                    end.linkTo(parent.end)
//                    width = Dimension.fillToConstraints
//                }
//            }
            ConstraintLayout(
                constraintSet = startSet,
                modifier = Modifier.fillMaxSize()
            ) {
                GroupsFilterItem(
                    filters = viewState.filterState,
                    onFilterCLicked = viewModel::toggleFilter,
                    modifier = Modifier.layoutId(filtersId)
                )

                LazyColumn(
                    modifier = Modifier.layoutId(buttonsId),
                    verticalArrangement = Arrangement.Center,
                ) {
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
