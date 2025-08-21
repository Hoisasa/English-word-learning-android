package com.sharksempire.englishcards.ui.composables.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import com.sharksempire.englishcards.ui.composables.GroupsViewState
import com.sharksempire.englishcards.ui.composables.Item
import com.sharksempire.englishcards.ui.composables.MainActivityViewModel
import com.sharksempire.englishcards.ui.theme.MyGreen
import com.sharksempire.englishcards.ui.theme.MyGreenText
import com.sharksempire.englishcards.ui.theme.MyPurple
import com.sharksempire.englishcards.ui.theme.MyPurpleShadow
import com.sharksempire.englishcards.ui.theme.groupsStyle

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
@Composable
fun Display_subgroups(
    onSubgroupSelected: () -> Unit,
    target: String,
    viewModel: MainActivityViewModel = hiltViewModel(),
) {
    LaunchedEffect(key1 = target, block = {viewModel.getSubgroups(target)} )
    val state by viewModel.uiState.collectAsState()
    
    when(val viewState = state) {
        GroupsViewState.Loading -> CircularProgressIndicator(modifier = Modifier.size(50.dp))
        is GroupsViewState.Error -> Text(text = viewState.message)
        is GroupsViewState.Success -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ){
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center
                ) {
                    val groups = viewState.content.filterIsInstance<Item.GroupsWithProgressData>()
                    items(
                        items = groups,
                        key = { it.name }
                    ) { group ->
                        Button(
                            onClick = {
                                viewModel.getSubgroups(group.name)
                                onSubgroupSelected()
                            },
                            shape = RoundedCornerShape(40.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 20.dp)
                                .shadow(
                                    6.dp,
                                    shape = RoundedCornerShape(40.dp)
                                )
                                .animateItem(),
                            // shadow with rounded corners
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MyPurple,
                                contentColor = MyGreenText,
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(end = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .shadow(6.dp, shape = CircleShape)
                                ) {
                                    CircularProgressIndicator(
                                        progress = { group.learned.toFloat() / group.total },
                                        modifier = Modifier.fillMaxSize(),
                                        strokeWidth = 13.dp,
                                        strokeCap = StrokeCap.Round,
                                        color = MyGreen
                                    )
                                }
                                
                                Spacer(modifier = Modifier.width(12.dp))
                                
                                Text(
                                    text = "${group.learned}/${group.total}",
                                    modifier = Modifier.weight(1f),
                                    color = MyGreenText,
                                    style = TextStyle(
                                        fontSize = 20.sp, fontWeight = FontWeight.SemiBold,
                                        shadow = Shadow(
                                            color = MyPurpleShadow,
                                            offset = Offset(-6f, 6f),  // adjust for shadow position
                                            blurRadius = 4f           // adjust for softness
                                        )
                                    )
                                )
                                
                                ScrollableTextWithArrow(
                                    text = group.name,
                                    modifier = Modifier
                                        .weight(10f),
                                    style = groupsStyle
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

