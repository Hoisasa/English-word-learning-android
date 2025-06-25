package com.example.myapplication.ui.composables

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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
@Preview(showBackground = true,
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
    val groups = remember { mutableStateListOf<String>() }
    val subgroups = remember { mutableStateListOf<String>() }
    var lessonWords = remember { mutableListOf<WordData>() }
    var grade by remember { mutableIntStateOf(0) }
    var endOfLesson by remember { mutableStateOf(false) }
    var lessonMistakes = remember { mutableStateListOf<WordData>() }
    
    
    val currentList = when (screenState) {
        "GroupsScreen" -> groups
        "SubGroupsScreen" -> subgroups
        else -> mutableStateListOf()
    }
    
    val handleSummaryExit: (Boolean) -> Unit = { value ->
        lessonMistakes.clear()
        endOfLesson = value
    }
    
    
    
    val context = LocalContext.current
    
    Surface(modifier) {
        when (screenState) {
            
            "GroupsScreen" -> {
                
                LaunchedEffect(Unit) {
                    groups.clear()
                    groups.addAll(queryOverDict("SELECT name FROM `groups`", null, context))
                }

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
                endOfLesson = false
                ModeSelectScreen(
                    {
                        chosenMode: String ->
                        studyMode = chosenMode
                        screenState = "LessonScreen"
                    },
                )
                BackHandler { screenState = "SubGroupsScreen" }
            }
            
            "LessonScreen" -> {
                
                if (!endOfLesson) {
                    lessonWords.clear()
                    lessonWords.addAll(queryWords(context, currentSubGroup))
                    StudyScreen(studyMode, lessonWords, lessonMistakes,
                        { value: Boolean, MODE: String, recievedGrade: Int ->
                            endOfLesson = value
                            studyMode = MODE
                            grade = recievedGrade
                        },
                    )
                } else {
                    BackHandler {
                        handleSummaryExit
                    }
//                    SummaryScreen(modifier, 100, lessonMistakes)
                    SummaryScreen(studyMode, grade, lessonMistakes, handleSummaryExit,
                        {
                            lessonMistakes.clear()
                            screenState = "ModeSelectScreen"
                        })
                    
                }
            }
        }
    }
}

fun queryOverDict(query: String, selectionArgs: String?, context: Context): List<String> {
    val result = mutableListOf<String>()
    val path = context.getDatabasePath("dictionary.db").absolutePath
    val db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY)
    val start = System.currentTimeMillis()
    
    val args = if (selectionArgs != null) arrayOf(selectionArgs) else selectionArgs
    val cursor = db.rawQuery(query, args)
    
    while (cursor.moveToNext()) {
        result.add(cursor.getString(0))
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
        val transcription = cursor.getStringOrNull(cursor.getColumnIndexOrThrow("transcription")) // can be null
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