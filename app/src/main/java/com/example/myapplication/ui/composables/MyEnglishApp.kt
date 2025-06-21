package com.example.myapplication.ui.composables

import android.annotation.SuppressLint
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.platform.LocalContext
import com.example.myapplication.ui.composables.screens.ModeSelectScreen
import androidx.compose.ui.tooling.preview.Preview
import com.example.myapplication.ui.composables.screens.OverviewScreen
import com.example.myapplication.ui.composables.screens.OverviewScreenPreview

@SuppressLint("UnrememberedMutableState")
@Preview(showBackground = true)
@Composable
fun MyEnglishApp(modifier: Modifier = Modifier) {
    
    var screenState by remember { mutableStateOf("LessonScreen") }
    var target by remember { mutableStateOf("") }
    var currentGroup by remember { mutableStateOf("") }
    var currentSubGroup by remember { mutableStateOf("") }
    var studyMode by remember { mutableStateOf("") }
    val groups = remember { mutableStateListOf<String>() }
    val subgroups = remember { mutableStateListOf<String>() }
    val lesson_words = remember { mutableStateListOf<List<String>>() }
    
    val currentList = when (screenState) {
        "GroupsScreen" -> groups
        "SubGroupsScreen" -> subgroups
        else -> mutableStateListOf()
    }
    
    val context = LocalContext.current
    
    Surface(modifier) {
        when (screenState) {
            
            "GroupsScreen" -> {
                
                LaunchedEffect(Unit) {
                    groups.clear()
                    groups.addAll(queryOverDict("SELECT name FROM `groups`", null, context))
                }
//
                Display_groups({ queryTarget: String ->
                    currentGroup = queryTarget
                    screenState = "SubGroupsScreen"
                }, currentList, modifier)
            }
            
            "SubGroupsScreen" -> {
                
                LaunchedEffect(currentGroup) {
                    subgroups.clear()
                    subgroups.addAll(
                        queryOverDict(
                            "SELECT * FROM subgroups WHERE group_id = ?",
                            currentGroup,
                            context
                        )
                    )
                }
                
                Display_groups({ queryTarget: String ->
                    currentSubGroup = queryTarget
                    screenState = "ModeSelectScreen"
                }, currentList, modifier)
                
                BackHandler { screenState = "GroupsScreen" }
                
            }
            
            "ModeSelectScreen" -> {
                ModeSelectScreen(
                    { chosenMode: String ->
                        screenState = chosenMode
                    },
                )
                BackHandler { screenState = "SubGroupsScreen" }
            }
            
            "LessonScreen" -> {
                OverviewScreenPreview()
//                LaunchedEffect(currentSubGroup) {
//                    lesson_words.clear()
//                    lesson_words.addAll(
//                        queryWords(
//                            "SELECT word, translation, weight FROM words WHERE subgroup_name = ?",
//                            currentSubGroup,
//                            context
//                        )
//                    )
//                }
//
//                when (studyMode) {
//                    "Overview" -> {
//                        OverviewScreen()
//                    }
//
//                    "Practice" -> {}
//                    "Exam" -> {}
//                }
                
                
            }
        }
    }
}

suspend fun queryOverDict(query: String, selectionArgs: String?, context: Context): List<String> {
    
    val result = mutableListOf<String>()
    val path = context.getDatabasePath("dictionary.db").absolutePath
    val db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY)
    
    val start = System.currentTimeMillis()
    
    val args = if (selectionArgs != null) arrayOf(selectionArgs) else selectionArgs
    val cursor = db.rawQuery(query, args)
    
    val end = System.currentTimeMillis()
    val elapsedMs = end - start
    Log.d("Timing", "Query took $elapsedMs ms")
    
    while (cursor.moveToNext()) {
        result.add(cursor.getString(0))
    }
    cursor.close()
    db.close()
    
    return result
}

suspend fun queryWords(
    query: String,
    selectionArgs: String?,
    context: Context
): List<List<String>> {
    val result = mutableListOf<List<String>>()
    val table = mutableListOf<String>()
    val path = context.getDatabasePath("dictionary.db").absolutePath
    val db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY)
    
    val start = System.currentTimeMillis()
    
    val args = if (selectionArgs != null) arrayOf(selectionArgs) else selectionArgs
    val cursor = db.rawQuery(query, args)
    
    val end = System.currentTimeMillis()
    val elapsedMs = end - start
    Log.d("Timing", "Query took $elapsedMs ms")
    
    while (cursor.moveToNext()) {
        for (i in 0 until cursor.columnCount) {
            table.add(cursor.getString(i) ?: "")
        }
        result.add(table)
    }
    
    cursor.close()
    db.close()
    
    return result
}