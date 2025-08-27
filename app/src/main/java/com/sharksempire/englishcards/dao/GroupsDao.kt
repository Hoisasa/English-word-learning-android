package com.sharksempire.englishcards.dao

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Query
import com.sharksempire.englishcards.ui.composables.Item

@Dao
interface GroupsDao {
    @Query(
"""
        SELECT
            main_groups.name AS name,
            COUNT(words.id) AS total_words,
            SUM(CASE WHEN words.weight = 1.0 THEN 1 ELSE 0 END) AS learned_words,
            main_groups.pos_name AS pos
        FROM main_groups
        JOIN subgroups ON subgroups.main_group_id = main_groups.name
        JOIN words ON words.subgroup_name = subgroups.name
        GROUP BY main_groups.name
        ORDER BY learned_words DESC
        """)
    suspend fun queryGroupsWithProgressData(): List<Item.GroupsWithProgressData>
    
    @Query(
        """
        SELECT
            subgroups.name AS name,
            COUNT(words.id) AS total_words,
            SUM(CASE WHEN words.weight = 1.0 THEN 1 ELSE 0 END) AS learned_words,
            pos.name AS pos
        FROM subgroups
        JOIN main_groups ON subgroups.main_group_id = main_groups.name
        JOIN pos ON main_groups.pos_name = pos.name
        JOIN words ON words.subgroup_name = subgroups.name
        WHERE subgroups.main_group_id = :mainGroup
        GROUP BY subgroups.name, pos.name
        """)
    suspend fun querySubgroupsWithProgressData(mainGroup: String): List<Item.GroupsWithProgressData>
    
    @Query(
        """
        SELECT
            subgroups.name AS name,
            COUNT(words.id) AS level,
            SUM(CASE WHEN words.weight = 1.0 THEN 1 ELSE 0 END) AS words_amount,
            pos.name AS pos
        FROM subgroups
        JOIN main_groups ON subgroups.main_group_id = main_groups.name
        JOIN pos ON main_groups.pos_name = pos.name
        JOIN words ON words.subgroup_name = subgroups.name
        GROUP BY subgroups.name, pos.name
        """
    )
    suspend fun querySpacedRepetitionGroups(): List<Item.SpacedRepetitionWordsWithLevel>
}



data class GroupsWithProgressData(
    val name: String,
    @ColumnInfo(name = "total_words") val total: Int,
    @ColumnInfo(name = "learned_words") val learned: Int,
    val pos: String)



//
//
//    fun updateLearnedCount(context: Context) {
//        val path = context.getDatabasePath("dictionary.db").absolutePath
//        val db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY)
//        val cursor = db.rawQuery(
//            """
//        SELECT SUM(CASE WHEN words.weight = 1.0 THEN 1 ELSE 0 END)
//        FROM words
//        WHERE words.subgroup_name = ?
//        """.trimIndent(),
//            arrayOf(name)
//        )
//        cursor.use {
//            if (it.moveToFirst()) {
//                learned = it.getInt(0)
//            }
//        }
//    }
//}
