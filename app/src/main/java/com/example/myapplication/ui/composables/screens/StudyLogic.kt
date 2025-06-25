package com.example.myapplication.ui.composables.screens

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import kotlin.math.roundToInt

fun getMark(isCorrect: Boolean, isExam: String): Float {
    val maxPoints = 5
    
    val multiplier: Int = when (isExam) {
        "Exam" -> 3
        else -> 1
    }
    
    val signed: Int = if (isCorrect) 1 else -1
    val mark = signed.toFloat() / maxPoints * multiplier
    return mark
}

fun updateQuery(wordId: Int, mark: Float, context: Context) {
    val path = context.getDatabasePath("dictionary.db").absolutePath
    val db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READWRITE)
    val stmt = db.compileStatement(
        """
    UPDATE words
    SET weight = CASE
        WHEN weight + ? > 1.0 THEN 1.0
        WHEN weight + ? < 0.0 THEN 0.0
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
    val max_points = 5
    return "⭐".repeat((weight * max_points).roundToInt())
}

//гордость скромность
fun calculateGrade(mistakes: Int, size: Int): Int {
    return (((size - mistakes).toFloat() / size) * 100).roundToInt()
}

data class WordData(
    val id: Int,
    val word: String,
    val translation: String,
    val transcription: String? = null,
    val weight: Float,
    val subgroup: String,
)

