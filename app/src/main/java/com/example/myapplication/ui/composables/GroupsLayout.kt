package com.example.myapplication.ui.composables

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.myapplication.ui.theme.MyGreen

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

@Preview
@Composable
fun Display_groups(buttonFunction: (String) -> Unit = {},
                   groupNames: SnapshotStateList<String> = mutableStateListOf("A", "B", "C", "D"),
                   modifier: Modifier = Modifier
) {
    
    LazyColumn(modifier = Modifier
        .fillMaxWidth()
    ) {
        items(groupNames) { name ->
            Button(
                onClick = {
                            buttonFunction(name)
                          },
                shape = RoundedCornerShape(30.dp),
                modifier = Modifier
                    .padding(5.dp)
                    .fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MyGreen,
                    contentColor = Color(0xFF555555),
                ),
            ) {
                Text(name)
            }
        }
    }
}


