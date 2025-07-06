package com.example.myapplication.ui.composables

import AudioDebugScreen
import android.annotation.SuppressLint
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.database.getStringOrNull
import com.example.myapplication.ui.composables.screens.GroupsWithProgressData
import com.example.myapplication.ui.composables.screens.ModeSelectScreen
import com.example.myapplication.ui.composables.screens.StudyScreen
import com.example.myapplication.ui.composables.screens.SummaryScreen
import com.example.myapplication.ui.composables.screens.WordData
import com.example.myapplication.ui.composables.screens.groupSaver
import com.example.myapplication.ui.composables.screens.groupsSaver
import com.example.myapplication.ui.composables.screens.queryOverDict
import com.example.myapplication.ui.composables.screens.queryWords
import com.example.myapplication.ui.composables.screens.updateWeightsOfLessonList
import com.example.myapplication.ui.composables.screens.wordDataSaver
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import reduceLearnedWordsTo1


@Preview(
    showBackground = true,
    device = Devices.TABLET,
    widthDp = 857,
    heightDp = 1370
)
@Composable
fun MyEnglishApp(modifier: Modifier = Modifier) {
    
    var screenState by rememberSaveable { mutableStateOf("GroupsScreen") }
    var currentGroup by rememberSaveable { mutableStateOf("") }
    var currentSubGroup by rememberSaveable(stateSaver = groupSaver) { mutableStateOf<GroupsWithProgressData?>(null) }
    var studyMode by rememberSaveable { mutableStateOf("") }
    val groups by rememberSaveable(stateSaver = groupsSaver) { mutableStateOf(mutableStateListOf()) }
    val lessonWords by rememberSaveable(stateSaver = wordDataSaver) { mutableStateOf(mutableStateListOf()) }
    var grade by rememberSaveable { mutableIntStateOf(0) }
    var restartRequested by rememberSaveable { mutableStateOf(true) }
    var isLoading by rememberSaveable { mutableStateOf(true) }
    var endOfLesson by rememberSaveable { mutableStateOf(false) }
    val lessonMistakes by rememberSaveable(stateSaver = wordDataSaver) { mutableStateOf(mutableStateListOf()) }
    
//    val currentList = when (screenState) {
//        "GroupsScreen" -> groups
//        else -> subgroups
//    }
    

    val context = LocalContext.current
    
    Surface(modifier) {
        when (screenState) {
            
            "AudioDebugging" -> AudioDebugScreen()
            
            "GroupsScreen", "SubGroupsScreen" -> {
                val sql: String
                val selectionArgs: String?
                val screenStateButton: (String) -> Unit
                
                if (screenState=="GroupsScreen") {
                    sql =
                        """
                        SELECT
                            main_groups.name AS name,
                            COUNT(words.id) AS total_words,
                            SUM(CASE WHEN words.weight = 1.0 THEN 1 ELSE 0 END) AS learned_words
                        FROM main_groups
                        JOIN subgroups ON subgroups.main_group_id = main_groups.name
                        JOIN words ON words.subgroup_name = subgroups.name
                        GROUP BY main_groups.name
                        ORDER BY main_groups.pos_name
                        """.trimIndent()
                    selectionArgs = null
                
                    screenStateButton = {
                        currentGroup = it
                        screenState = "SubGroupsScreen"}
                } else {
                    sql =
                        """
                        SELECT
                            subgroups.name AS name,
                            COUNT(words.id) AS total_words,
                            SUM(CASE WHEN words.weight = 1.0 THEN 1 ELSE 0 END) AS learned_words
                        FROM subgroups
                        JOIN words ON words.subgroup_name = subgroups.name
                        WHERE subgroups.main_group_id = ?
                        GROUP BY subgroups.name
                        """.trimIndent()
                    selectionArgs = currentGroup
                    screenStateButton = { queryTarget: String ->
                        currentSubGroup = groups.find { it.name == queryTarget }!!
                        screenState = "ModeSelectScreen"
                    }
                    
                BackHandler { screenState = "GroupsScreen" }
                }
                
                LaunchedEffect(selectionArgs) {
                    groups.clear()
                    groups.addAll(queryOverDict(context, sql, selectionArgs))
                }
                
                Display_groups(screenStateButton, groups, modifier)
            }
            
            "ModeSelectScreen" -> {
                // Words should not persist across different modes
                lessonWords.clear()
                restartRequested = true
                isLoading = true
                
                
                endOfLesson = false
                ModeSelectScreen(
                    {
                        studyMode = it
                        screenState = "LessonScreen"
                    },
                    currentSubGroup
                )
                
                BackHandler { screenState = "SubGroupsScreen" }
            }
            
            "LessonScreen" -> {
                Log.d("LoadingLesson", "We entered lesson screen")
                
                
                LaunchedEffect(restartRequested) {
                    if (restartRequested) {
                        isLoading = true
                        Log.d("LoadingLesson", "load start")
                        if (lessonWords.isNotEmpty()) { //then its not the first time we entered the lesson
                            updateWeightsOfLessonList(lessonWords, context, currentSubGroup!!.name)
                            Log.d("LoadingLesson", "Weight update")
                        } else {
                            lessonWords.addAll(queryWords(context, currentSubGroup!!.name))
                            Log.d("LoadingLesson", "full requery")
                        }
                        if (studyMode=="Practice") { lessonWords.reduceLearnedWordsTo1() }
                        if (studyMode!="Overview") { lessonWords.shuffle() } // to be replaced with smart shuffle
                        Log.d("Shuffle", "Lesson ready: ${lessonWords.joinToString { it.word }}")
                        lessonMistakes.clear()
                        endOfLesson = false // only here if it's a clean fresh start
                        restartRequested = false
                        isLoading = false
                        Log.d("LoadingLesson", "load end")
                    }
                }
                
                if (isLoading) {
                    Log.d("LoadingLesson", "We want list of words NOW")
                } else {
                    if (!endOfLesson) {
                        StudyScreen(
                            studyMode, lessonWords, lessonMistakes,
                            {
                                grade = it
                                endOfLesson = true
                            },
                            { restartRequested = true }
                        )
                    } else {
                        SummaryScreen(
                            studyMode,
                            grade,
                            currentSubGroup!!.name,
                            lessonMistakes,
                            { restartRequested = true },
                            { screenState = "ModeSelectScreen" }
                        )
                    }
                }
                
                BackHandler {
                    screenState = "ModeSelectScreen"
                }
            }
        }
    }
}
