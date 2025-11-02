package com.sharksempire.englishcards.dao

import androidx.room.Dao
import androidx.room.Query
import com.sharksempire.englishcards.ui.composables.GroupsWithProgressData
import com.sharksempire.englishcards.ui.composables.SpacedRepetitionLevelsWithDateData

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
    suspend fun queryGroupsWithProgressData(): List<GroupsWithProgressData>
    
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
    suspend fun querySubgroupsWithProgressData(mainGroup: String): List<GroupsWithProgressData>
    
    @Query(
        """
    SELECT
        'Group ' || level || ': repeat after ' ||
            CASE level
                WHEN 1 THEN '1 day'
                WHEN 2 THEN '3 days'
                WHEN 3 THEN '7 days'
                WHEN 4 THEN '14 days'
                WHEN 5 THEN '30 days'
                ELSE '0 days'
            END AS name,
        level,
        COUNT(*) AS total_words,
        SUM(
            CASE
                WHEN date(exam_completed_at, 'localtime') <= date(
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
                ) THEN 1
                ELSE 0
            END
        ) AS due_words
    FROM words
    WHERE level > 0
    GROUP BY level
    ORDER BY level
    """
    )
    suspend fun querySpacedRepetitionWordsWithLevel(): List<SpacedRepetitionLevelsWithDateData>
}
