package com.sharksempire.englishcards.ui.composables.screens

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import androidx.compose.ui.graphics.Color
import com.sharksempire.englishcards.ui.theme.GradeColorHigh
import com.sharksempire.englishcards.ui.theme.GradeColorLow
import com.sharksempire.englishcards.ui.theme.GradeColorMedium
import com.sharksempire.englishcards.ui.theme.GradeColorPerfect

fun getGradeColor(lessonScore: Int): Color {
    return when {
        lessonScore == 100 -> GradeColorPerfect
        lessonScore >= 80 -> GradeColorHigh
        lessonScore >= 60 -> GradeColorMedium
        else -> GradeColorLow
    }
}

fun getGrades(db: SQLiteDatabase, subgroupName: String): List<Int> {
    val grades = mutableListOf<Int>()
    val cursor = db.rawQuery(
        "SELECT grade FROM grades WHERE subgroup_name = ? ORDER BY epoch DESC",
        arrayOf(subgroupName)
    )
    while (cursor.moveToNext()) {
        grades.add(cursor.getInt(0))
    }
    cursor.close()
    return grades
}

fun insertGrade(
    db: SQLiteDatabase,
    subgroupName: String,
    grade: Int,
    epoch: Long = System.currentTimeMillis()
) {
    val values = ContentValues().apply {
        put("subgroup_name", subgroupName)
        put("grade", grade)
        put("epoch", epoch)
    }
    db.insert("grades", null, values)
}

fun cleanupOldGrades(db: SQLiteDatabase, subgroupName: String) {
    val sql = """
        DELETE FROM grades WHERE id NOT IN (
            SELECT id FROM grades WHERE subgroup_name = ? ORDER BY epoch DESC LIMIT 4
        ) AND subgroup_name = ?
    """.trimIndent()
    
    db.execSQL(sql, arrayOf(subgroupName, subgroupName))
}

fun markExamCompleted(db: SQLiteDatabase, subgroupName: String) {
    val stmt = db.compileStatement(
        """
        UPDATE subgroups
        SET exam_completed_at = CURRENT_TIMESTAMP,
            level = level + 1
        WHERE name = ?
        """.trimIndent()
    )
    stmt.bindString(1, subgroupName)
    stmt.executeUpdateDelete()
    stmt.close()
}

