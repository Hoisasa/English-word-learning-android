package com.sharksempire.englishcards.dao

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.dict.db.GradeEntity

@Dao
interface WordsDao {
    
    @Query(
        """
        SELECT id, word, translation, transcription, weight, subgroup_name
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
            AND level <= 0
        """)
    suspend fun markExamCompleted(subGroup: String)
    
    @Query("SELECT grade FROM grades WHERE subgroup_name = :subgroupName AND mode = :mode ORDER BY epoch DESC")
    suspend fun getGrades(subgroupName: String, mode: String): List<Int>
    
    @Insert
    suspend fun insertGrade(grade: GradeEntity)
    
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
    
    @Query(
        """
    UPDATE words
    SET level = CASE
        WHEN :isCorrect AND level < 5 THEN level + 1
        WHEN NOT :isCorrect AND level > 1 THEN level - 1
        ELSE level
        
    END,
        exam_completed_at = CURRENT_TIMESTAMP
    WHERE id = :wordId
    """
    )
    suspend fun queryUpdateLevel(wordId: Int, isCorrect: Boolean)
    

    @Query(
        """
    SELECT
        id,
        word,
        translation,
        transcription,
        weight,
        subgroup_name
    FROM words
    WHERE
        (:subgroupTarget IS NULL OR subgroup_name = :subgroupTarget)
      AND date(exam_completed_at, 'localtime') <= date(
            'now',
            '-3 hours',
            CASE level
                WHEN 1 THEN '-1 day'
                WHEN 2 THEN '-3 days'
                WHEN 3 THEN '-7 days'
                WHEN 4 THEN '-14 days'
                WHEN 5 THEN '-30 days'
                ELSE '0 days'
            END,
            'localtime'
        )
      AND level = :levelTarget
    """
    )
    suspend fun queryReviewWords(levelTarget: Int, subgroupTarget: String?): List<WordData>
}

data class WordData(
    val id: Int,
    val word: String,
    val translation: String,
    val transcription: String? = null,
    var weight: Float,
    @ColumnInfo(name = "subgroup_name") val subgroupName: String,
)

data class WordWeight(
    val id: Int,
    val weight: Float,
)