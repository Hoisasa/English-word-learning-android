package com.example.myapplication.ui.composables

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.example.myapplication.ui.composables.screens.GroupsScreen
import com.example.myapplication.ui.composables.screens.SubGroupsScreen
import com.example.myapplication.ui.composables.screens.ModeSelectScreen
import android.util.Log
import androidx.compose.runtime.snapshots.Snapshot
import androidx.compose.runtime.snapshots.SnapshotStateList


@Composable
fun MyEnglishApp(modifier: Modifier = Modifier) {
    
    var screenState by remember { mutableStateOf("GroupsScreen") }
    var target by remember { mutableStateOf("") }
    var groups = remember { mutableStateListOf<String>() }
    var subgroups = remember { mutableStateListOf<String>() }
    var lesson_words = remember { mutableStateListOf<String>() }
    
    val context = LocalContext.current
    LaunchedEffect(screenState, target, subgroups, lesson_words) {
        val names = mutableStateListOf<String>()
        
        val path = context.getDatabasePath("dictionary.db").absolutePath
        val db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY)
        
        val start = System.currentTimeMillis()
        
        val query = "SELECT name FROM `groups`"
        val cursor = db.rawQuery(query, null)
        
        val end = System.currentTimeMillis()
        val elapsedMs = end - start
        Log.d("Timing", "Query took $elapsedMs ms")
        
        while (cursor.moveToNext()) {
            names.add(cursor.getString(0))
        }
        cursor.close()
        db.close()
        
        
        when (screenState) {
            "GroupsScreen" -> { groups.clear(); groups.addAll(names) }
            "SubGroupsScreen" -> { subgroups.clear(); subgroups.addAll(names) }
            "ModeSelectScreen" -> { lesson_words.clear(); lesson_words.addAll(names) }
        }
    }
    
    Surface(modifier) {
        when (screenState) {
            "GroupsScreen" -> GroupsScreen(
                onGroupSelect = { queryTarget: String ->
                    target = queryTarget
                    screenState = "SubGroupSelect"
                }, groups,
                modifier
            )
            
            "SubGroupsScreen" -> SubGroupsScreen(
                onSubGroupSelect = { queryTarget: String ->
                    target = queryTarget
                    screenState = "ModeSelectScreen"
                },
                subgroups,
                modifier,
            )
            
            "ModeSelectScreen" -> ModeSelectScreen(
                onModeChosen = { screenState = "LessonScreen" },
                modifier
            )
        }
    }
}
