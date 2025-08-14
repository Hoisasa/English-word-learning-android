package com.sharksempire.englishcards.ui.composables.screens

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.core.database.getStringOrNull
import com.sharksempire.englishcards.ui.theme.MyGreen
import com.sharksempire.englishcards.ui.theme.MyPurpleShadow
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Composable
fun ScrollableTextWithArrow(text: String, modifier: Modifier = Modifier, style: TextStyle) {
    val scrollState = rememberScrollState()
    Box( modifier = modifier) {
        Text(
            text = text,
            modifier = Modifier
                .horizontalScroll(scrollState)
                .padding(top = 15.dp, bottom = 10.dp),
            style = style
        )
        
        // The indicator of a text not fitting in the line
        // is int.MAX_VALUE on recomposition -> we cut that one out to avoid flickery
        
        if (scrollState.maxValue != Int.MAX_VALUE && (scrollState.value + 10) < scrollState.maxValue) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                tint = MyGreen,
                contentDescription = "Scroll right",
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(50.dp)
                    .padding(4.dp)
                    .clip(CircleShape)
                    
                    .background(MyPurpleShadow.copy(alpha = 0.7f))
            )
        }
    }
}


fun queryOverDict(context: Context, query: String, selectionArgs: String? = null): List<GroupsWithProgressData> {
    val result = mutableListOf<GroupsWithProgressData>()
    val path = context.getDatabasePath("dictionary.db").absolutePath
    val db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY)
    val start = System.currentTimeMillis()
    
    val args = if (selectionArgs != null) arrayOf(selectionArgs) else selectionArgs
    val cursor = db.rawQuery(query, args)
    
    while (cursor.moveToNext()) {
        val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
        val total = cursor.getInt(cursor.getColumnIndexOrThrow("total_words"))
        val learned = cursor.getInt(cursor.getColumnIndexOrThrow("learned_words"))
        val pos = cursor.getString(cursor.getColumnIndexOrThrow("pos"))
        val level = cursor.getInt(cursor.getColumnIndexOrThrow("level"))
        
        val data = GroupsWithProgressData(name, total, learned, pos, level)
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
    
    val query = "SELECT id, weight FROM words WHERE subgroup_name = ? ORDER BY id"
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

data class GroupsWithProgressData(val name: String, val total: Int, var learned: Int, val pos: String, val level: Int) {
    fun updateLearnedCount(context: Context) {
        val path = context.getDatabasePath("dictionary.db").absolutePath
        val db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY)
        val cursor = db.rawQuery(
            """
        SELECT SUM(CASE WHEN words.weight = 1.0 THEN 1 ELSE 0 END)
        FROM words
        WHERE words.subgroup_name = ?
        """.trimIndent(),
            arrayOf(name)
        )
        cursor.use {
            if (it.moveToFirst()) {
                learned = it.getInt(0)
            }
        }
    }
}

val groupsSaver = Saver<SnapshotStateList<GroupsWithProgressData>, String>(
    save = { list -> Gson().toJson(list) },
    restore = { json ->
        val type = object : TypeToken<List<GroupsWithProgressData>>() {}.type
        Gson().fromJson<List<GroupsWithProgressData>>(json, type).toMutableStateList()
    }
)

val groupSaver = Saver<GroupsWithProgressData?, String>(
    save = { group -> Gson().toJson(group) },
    restore = { json -> Gson().fromJson(json, GroupsWithProgressData::class.java) }
)

fun toggleFilter(pos: String, selectedItems: ArrayList<String>, allPOS: ArrayList<String>): ArrayList<String> {
    val setOfChosenFilters = selectedItems.toSet()
    val setOfAllFilters = allPOS.toSet()
    
    val newGroups = (if (setOfChosenFilters == setOfAllFilters) {
        arrayListOf(pos)
    } else if (pos in setOfChosenFilters) {
        if (setOfChosenFilters == arrayListOf(pos).toSet()) {
            setOfAllFilters
        } else {
            setOfChosenFilters - pos
        }
    } else {
        setOfChosenFilters + pos
    })
    
    return ArrayList(newGroups)
}

fun extendDB(context: Context) {
    val path = context.getDatabasePath("dictionary.db").absolutePath
    val db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READWRITE)
    
    db.use {
        it.execSQL("ALTER TABLE subgroups ADD COLUMN level INTEGER NOT NULL DEFAULT 0")
        it.execSQL("UPDATE subgroups SET level = 1 WHERE exam_completed = 1")
    }
}