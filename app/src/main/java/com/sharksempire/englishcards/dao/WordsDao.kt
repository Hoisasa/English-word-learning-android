package com.sharksempire.englishcards.dao

import androidx.room.Dao
import androidx.room.Query

@Dao
interface WordsDao {
    
    @Query("SELECT id, word, translation, transcription, weight" +
            " FROM words WHERE subgroup_name = :subGroup ORDER BY weight")
    fun queryWords(subGroup: String): List<WordData>
    
//    @Query("SELECT id, weight FROM words WHERE subgroup_name = :subGroup ORDER BY id")
//    fun updateWeights(subGroup: String)
    
}

data class WordData(
    val id: Int,
    val word: String,
    val translation: String,
    val transcription: String? = null,
    var weight: Float,
)