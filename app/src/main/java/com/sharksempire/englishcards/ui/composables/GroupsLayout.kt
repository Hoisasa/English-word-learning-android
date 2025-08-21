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
import com.sharksempire.englishcards.ui.composables.screens.toggleFilter
import com.sharksempire.englishcards.ui.theme.MyGreen
import com.sharksempire.englishcards.ui.theme.MyGreenText
import com.sharksempire.englishcards.ui.theme.MyPurple
import com.sharksempire.englishcards.ui.theme.MyPurpleShadow

//Perfect use case for a lazy-loaded button with deferred logic. Here's how you can design it:

//💡 Behavior summary:
//✅ Button shows: "Repeat Groups (3)"
//⏳ Calculation runs only once per app start — and only if pressed (or eventually, when idle)
//🔁 The count updates daily at most
//🧘 Doesn't block or interrupt anything

//🛠️ Suggested structure:
//Button text state: initially "Repeat Groups"
//On press: if the count hasn't been fetched yet, trigger the query (in a coroutine)
//Idle background check: if you want it to preload, queue it with something like lifecycleScope.launch { delay(5_000); if (notFetched) fetch() }

//🧠 Bonus ideas:
//Cache the value in ViewModel, DB, or file with a timestamp → only refresh once a day.
//While waiting: show loading spinner or Repeat Groups (...).

//Let me know if you're in Compose, classic XML + ViewModel, or Jetpack + LiveData — I'll shape the idea accordingly.

//// Helper to get cached count and last update date
//fun getCachedRepeatCount(context: Context): Pair<Int?, Long?> {
//    val prefs = context.getSharedPreferences("cache_prefs", Context.MODE_PRIVATE)
//    val count = prefs.getInt("repeat_count", -1).takeIf { it >= 0 }
//    val timestamp = prefs.getLong("repeat_count_timestamp", 0L).takeIf { it > 0 }
//    return Pair(count, timestamp)
//}
//
//fun saveCachedRepeatCount(context: Context, count: Int) {
//    val prefs = context.getSharedPreferences("cache_prefs", Context.MODE_PRIVATE)
//    prefs.edit()
//        .putInt("repeat_count", count)
//        .putLong("repeat_count_timestamp", System.currentTimeMillis())
//        .apply()
//}
//
//// Usage in your button logic
//val (cachedCount, cachedTime) = getCachedRepeatCount(context)
//val oneDayMillis = 24 * 60 * 60 * 1000L
//val isCacheValid = cachedTime != null && (System.currentTimeMillis() - cachedTime) < oneDayMillis
//
//val displayCount = if (isCacheValid && cachedCount != null) cachedCount else null
//
//button.text = if (displayCount != null) "Repeat Groups ($displayCount)" else "Repeat Groups"
//
//// On button click, if no valid cache, launch coroutine to fetch count, save to prefs, update button text

sealed class GroupsFilter(val showFilter: Boolean) {
    object Main: GroupsFilter(true)
    object Sub: GroupsFilter(false)
    object Review: GroupsFilter(true)
}

sealed interface GroupsViewState {
    object Loading : GroupsViewState
    data class Error(val message: String) : GroupsViewState
    data class Success(
        val showFilter: Boolean,
        val content: List<Item>
    ) : GroupsViewState
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
                    arrayListOf<String>("verb", "noun", "adjective", "preposition"),
                    arrayListOf<String>("verb", "noun", "adjective", "preposition"),
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
                                pos = "outclass"
                            ),
                            colors = listOf(MyGreen, MyPurpleShadow, MyGreen, MyGreenText),
                        )
                    }
                    
                    val groups = viewState.content.filterIsInstance<Item.GroupsWithProgressData>()
                    items(
                        items = groups,
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
