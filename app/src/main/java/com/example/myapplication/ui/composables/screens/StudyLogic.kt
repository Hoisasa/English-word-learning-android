package com.example.myapplication.ui.composables.screens

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.media.MediaPlayer
import android.os.Build
import android.util.Log
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlin.math.roundToInt


private var activePlayer: MediaPlayer? = null
const val MAX_POINTS = 5


fun getMark(isCorrect: Boolean, isExam: String): Float {
    
    val multiplier: Int = when (isExam) {
        "Exam" -> 3
        else -> 1
    }
    
    val signed: Int = if (isCorrect) 1 else -1
    val mark = signed.toFloat() / MAX_POINTS * multiplier
    return mark
}

fun updateQuery(wordId: Int, mark: Float, context: Context) {
    val path = context.getDatabasePath("dictionary.db").absolutePath
    val db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READWRITE)
    val stmt = db.compileStatement(
        """
    UPDATE words
    SET weight = CASE
        WHEN weight + ? > 0.9999 THEN 1.0
        WHEN weight + ? < 0.0001 THEN 0.0
        ELSE weight + ?
    END
    WHERE id = ?
    """.trimIndent()
    )
    
    stmt.bindDouble(1, mark.toDouble()) // for weight + ?
    stmt.bindDouble(2, mark.toDouble()) // for weight + ?
    stmt.bindDouble(3, mark.toDouble()) // for weight + ?
    stmt.bindLong(4, wordId.toLong())   // for id = ?
    
    val updatedRows = stmt.executeUpdateDelete()
    Log.d("queries", "updated rows: $updatedRows")
    stmt.close()
}


fun updateWeight(isCorrect: Boolean, isExam: String, wordId: Int, context: Context) {
    val mark = getMark(isCorrect, isExam)
    val start = System.currentTimeMillis()
    
    updateQuery(wordId, mark, context)
    
    val end = System.currentTimeMillis()
    val elapsedMs = end - start
    Log.d("Timing", "Weight update took $elapsedMs ms")
}


fun showPoints(weight: Float): String {
    return "⭐".repeat((weight * MAX_POINTS).roundToInt())
}


fun calculateGrade(mistakes: Int, size: Int): Int {
    return (((size - mistakes).toFloat() / size) * 100).roundToInt()
}


fun playOggFromAssets(context: Context, assetPath: String) {
    try {
        // Stop currently playing audio
        activePlayer?.stop()
        activePlayer?.release()

        // Set up new media player
        val afd = context.assets.openFd(assetPath)
        val mediaPlayer = MediaPlayer().apply {
            setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            prepare()
            start()
            setOnCompletionListener {
                it.release()
                if (activePlayer === it) activePlayer = null
            }
        }

        afd.close()
        activePlayer = mediaPlayer

    } catch (e: Exception) {
        Log.e("AudioPlay", "Error playing audio $assetPath", e)
    }
}


fun buildAssetFilePath(subGroup: String, wordName: String): String {
    val safeSubGroup = subGroup.replace("/", "-").replace(":", "-")
    val safeWordName = wordName.split(" (")[0]
    return "audiofiles/$safeSubGroup/$safeWordName.ogg"
}



data class WordData(
    val id: Int,
    val word: String,
    val translation: String,
    val transcription: String? = null,
    var weight: Float,
    val subgroup: String,
)

val wordDataSaver = Saver<SnapshotStateList<WordData>, String>(
    save = { list -> Gson().toJson(list) },
    restore = { json ->
        val type = object : TypeToken<List<WordData>>() {}.type
        Gson().fromJson<List<WordData>>(json, type).toMutableStateList()
    }
)
