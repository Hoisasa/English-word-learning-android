package com.sharksempire.englishcards.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.dict.db.GradeEntity

@Dao
interface WordsDao {
    
    @Query(
        """
        SELECT id, word, translation, transcription, weight
        FROM words
        WHERE subgroup_name = :subGroup
        ORDER BY weight""")
    suspend fun queryWords(subGroup: String): List<WordData>
    
    @Query("SELECT id, weight FROM words WHERE subgroup_name = :subGroup ORDER BY id")
    suspend fun queryNewWeights(subGroup: String): List<WordWeight>

    @Query(
        """
        UPDATE words
        SET weight = CASE
            WHEN weight + :mark > 0.9999 THEN 1.0
            WHEN weight + :mark < 0.0001 THEN 0.0
            ELSE weight + :mark
        END
        WHERE id = :wordId
        """
    )
    suspend fun queryUpdateWeight(wordId: Int, mark: Float)
    
    @Query(
        """
        UPDATE words
        SET exam_completed_at = CURRENT_TIMESTAMP,
            level = 2
        WHERE subgroup_name = :subGroup
        """)
    suspend fun markExamCompleted(subGroup: String)
    
    @Query("SELECT grade FROM grades WHERE subgroup_name = :subgroupName AND mode = :mode ORDER BY epoch DESC")
    suspend fun getGrades(subgroupName: String, mode: String): List<Int>
    
    // 2) Insert a grade
    @Insert
    suspend fun insertGrade(grade: GradeEntity)
    
    // 3) Cleanup old grades (keep only latest 4)
    @Query(
        """
        DELETE FROM grades
        WHERE id NOT IN (
            SELECT id FROM grades
            WHERE subgroup_name = :subgroupName AND mode = :mode
            ORDER BY epoch DESC LIMIT 4
        )
        AND subgroup_name = :subgroupName
        AND mode = :mode
        """)
    suspend fun cleanupOldGrades(subgroupName: String, mode: String)
}

data class WordData(
    val id: Int,
    val word: String,
    val translation: String,
    val transcription: String? = null,
    var weight: Float,
)

data class WordWeight(
    val id: Int,
    val weight: Float,
)