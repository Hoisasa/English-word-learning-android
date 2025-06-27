package com.example.myapplication.ui.composables

import AudioDebugScreen
import android.annotation.SuppressLint
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.database.getStringOrNull
import com.example.myapplication.ui.composables.screens.ModeSelectScreen
import com.example.myapplication.ui.composables.screens.StudyScreen
import com.example.myapplication.ui.composables.screens.SummaryScreen
import com.example.myapplication.ui.composables.screens.WordData




@SuppressLint("UnrememberedMutableState")
@Preview(
    showBackground = true,
    device = Devices.TABLET,
    widthDp = 857,
    heightDp = 1370
)
@Composable
fun MyEnglishApp(modifier: Modifier = Modifier) {
    
    var screenState by remember { mutableStateOf("GroupsScreen") }
    var currentGroup by remember { mutableStateOf("") }
    var currentSubGroup by remember { mutableStateOf("") }
    var studyMode by remember { mutableStateOf("") }
    val groups = remember { mutableStateListOf<GroupesWithProgressData>() }
    val subgroups = remember { mutableStateListOf<GroupesWithProgressData>() }
    var lessonWords = remember { mutableListOf<WordData>() }
    var grade by remember { mutableIntStateOf(0) }
    var restartRequested by remember { mutableStateOf(true) }
    var isLoading by remember { mutableStateOf(true) }
    var endOfLesson by remember { mutableStateOf(false) }
    var lessonMistakes = remember { mutableStateListOf<WordData>() }
    
    val currentList = when (screenState) {
        "GroupsScreen" -> groups
        "SubGroupsScreen" -> subgroups
        else -> mutableStateListOf()
    }
    

    val context = LocalContext.current
    
    Surface(modifier) {
        when (screenState) {
            
            "AudioDebugging" -> AudioDebugScreen()
            
            "GroupsScreen" -> {
                
                val sql = """
                    SELECT
                        `groups`.name AS name,
                        COUNT(words.id) AS total_words,
                        SUM(CASE WHEN words.weight = 1.0 THEN 1 ELSE 0 END) AS learned_words
                    FROM `groups`
                    JOIN subgroups ON subgroups.group_id = `groups`.name
                    JOIN words ON words.subgroup_name = subgroups.name
                    GROUP BY `groups`.name
                    ORDER BY `groups`.pos_name
                """.trimIndent()
                
                
                LaunchedEffect(Unit) {
                    groups.clear()
                    groups.addAll(queryOverDict(context, sql))}
                

                Display_groups({ queryTarget: String ->
                    currentGroup = queryTarget
                    screenState = "SubGroupsScreen"
                }, currentList, modifier)
            }
            
            "SubGroupsScreen" -> {
                
                val sql =   """
                            SELECT
                                subgroups.name AS name,
                                COUNT(words.id) AS total_words,
                                SUM(CASE WHEN words.weight = 1.0 THEN 1 ELSE 0 END) AS learned_words
                            FROM subgroups
                            JOIN words ON words.subgroup_name = subgroups.name
                            WHERE subgroups.group_id = ?
                            GROUP BY subgroups.name
                            """.trimIndent()
                
                
                LaunchedEffect(currentGroup) {
                    subgroups.clear()
                    subgroups.addAll(
                        queryOverDict(
                            context,
                            sql,
                            currentGroup,
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
                // Words should not persist across different modes
                lessonWords.clear()
                restartRequested = true
                isLoading = true
                
                
                endOfLesson = false
                ModeSelectScreen(
                    { chosenMode: String ->
                        studyMode = chosenMode
                        screenState = "LessonScreen"
                    },
                )
                BackHandler { screenState = "SubGroupsScreen" }
            }
            
            "LessonScreen" -> {
                Log.d("LoadingLesson", "We entered lesson screen")
                
                BackHandler {
                    screenState = "ModeSelectScreen"
                }
                
                
                LaunchedEffect(restartRequested) {
                    if (restartRequested) {
                        isLoading = true
                        Log.d("LoadingLesson", "load start")
                        if (lessonWords.size != 0) { //then its not the first time we entered the lesson
                            updateWeightsOfLessonList(lessonWords, context, currentSubGroup)
                            Log.d("LoadingLesson", "Weight update")
                        } else {
                            lessonWords.addAll(queryWords(context, currentSubGroup))
                            Log.d("LoadingLesson", "full requery")
                        }
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
                            { value: Boolean, MODE: String, receivedGrade: Int ->
                                endOfLesson = value
                                studyMode = MODE
                                grade = receivedGrade
                            },
                            {
                                restartRequested = true
                            }
                        )
                    } else {
                        SummaryScreen(
                            studyMode,
                            grade,
                            currentSubGroup,
                            lessonMistakes,
                            {
                                lessonMistakes.clear()
                                endOfLesson = false
                            },
                            {
                                lessonMistakes.clear()
                                screenState = "ModeSelectScreen"
                            }
                        )
                    }
                }
            }
        }
    }
}

fun queryOverDict(context: Context, query: String, selectionArgs: String? = null): List<GroupesWithProgressData> {
    val result = mutableListOf<GroupesWithProgressData>()
    val path = context.getDatabasePath("dictionary.db").absolutePath
    val db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY)
    val start = System.currentTimeMillis()
    
    val args = if (selectionArgs != null) arrayOf(selectionArgs) else selectionArgs
    val cursor = db.rawQuery(query, args)
    
    while (cursor.moveToNext()) {
        val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
        val total = cursor.getInt(cursor.getColumnIndexOrThrow("total_words"))
        val learned = cursor.getInt(cursor.getColumnIndexOrThrow("learned_words"))
        
        val data = GroupesWithProgressData(name, total, learned)
        result.add(data)
    }
    cursor.close()
    db.close()
    
    val end = System.currentTimeMillis()  //wrap
    val elapsedMs = end - start
    Log.d("Timing", "Query for groups took $elapsedMs ms")
    
    return result
}

fun queryWords(
    context: Context,
    selectionArgs: String,
): List<WordData> {
    val result = mutableListOf<WordData>()
    val path = context.getDatabasePath("dictionary.db").absolutePath
    val db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY)
    
    val start = System.currentTimeMillis()
    
    val query = "SELECT * FROM words WHERE subgroup_name = ? ORDER BY weight"
    val cursor = db.rawQuery(query, arrayOf(selectionArgs))
    
    while (cursor.moveToNext()) {
        val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
        val word = cursor.getString(cursor.getColumnIndexOrThrow("word")) ?: ""
        val translation = cursor.getString(cursor.getColumnIndexOrThrow("translation")) ?: ""
        val transcription =
            cursor.getStringOrNull(cursor.getColumnIndexOrThrow("transcription")) // can be null
        val weight = cursor.getFloat(cursor.getColumnIndexOrThrow("weight"))
        val subgroup = cursor.getString(cursor.getColumnIndexOrThrow("subgroup_name"))
        
        val wordData = WordData(id, word, translation, transcription, weight, subgroup)
        result.add(wordData)
    }
    
    val end = System.currentTimeMillis()
    val elapsedMs = end - start
    Log.d("Timing", "Query for words took $elapsedMs ms")
    
    cursor.close()
    db.close()
    
    return result
}

fun updateWeightsOfLessonList(
    lessonWords: MutableList<WordData>,
    context: Context,
    selectionArgs: String,
) {
    val path = context.getDatabasePath("dictionary.db").absolutePath
    val db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY)
    
    val start = System.currentTimeMillis()
    
    val query = "SELECT id, weight FROM words WHERE subgroup_name = ? ORDER BY weight"
    val cursor = db.rawQuery(query, arrayOf(selectionArgs))
    
    while (cursor.moveToNext()) {
        val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
        val weight = cursor.getFloat(cursor.getColumnIndexOrThrow("weight"))
        
        for(word in lessonWords){
            if (word.id == id) {
                word.weight = weight
            }
        }
    }
    
    val end = System.currentTimeMillis()
    val elapsedMs = end - start
    Log.d("Timing", "Query for words took $elapsedMs ms")
    
    cursor.close()
    db.close()
}

data class GroupesWithProgressData(val name: String, val total: Int, val learned: Int)