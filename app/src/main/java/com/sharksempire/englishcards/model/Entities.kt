package com.example.dict.db

import androidx.room.*

@Entity(tableName = "pos")
data class POSEntity(
    @PrimaryKey val name: String
)

@Entity(
    tableName = "main_groups",
    foreignKeys = [ForeignKey(
        entity = POSEntity::class,
        parentColumns = ["name"],
        childColumns = ["pos_name"],
        onDelete = ForeignKey.NO_ACTION,
        onUpdate = ForeignKey.NO_ACTION
    )]
)
data class GroupEntity(
    @PrimaryKey val name: String,
    @ColumnInfo(name = "pos_name") val posName: String?          // NOT NULL via SQLAlchemy
)

@Entity(
    tableName = "subgroups",
    foreignKeys = [ForeignKey(
        entity = GroupEntity::class,
        parentColumns = ["name"],
        childColumns = ["main_group_id"],
        onDelete = ForeignKey.NO_ACTION,
        onUpdate = ForeignKey.NO_ACTION
    )]
)
data class SubGroupEntity(
    @PrimaryKey val name: String,
    @ColumnInfo(name = "main_group_id") val mainGroupId: String?,      // NOT NULL via SQLAlchemy
    @ColumnInfo(name = "exam_completed_at") val examCompletedAt: Int?, // store as epoch millis
)

@Entity(
    tableName = "words",
    foreignKeys = [ForeignKey(
        entity = SubGroupEntity::class,
        parentColumns = ["name"],
        childColumns = ["subgroup_name"],
        onDelete = ForeignKey.NO_ACTION,
        onUpdate = ForeignKey.NO_ACTION
    )],
    indices = [Index("subgroup_name")]
)
data class WordEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val word: String,
    val translation: String,
    val transcription: String?,
    val weight: Float = 0f,
    @ColumnInfo(name = "exam_completed_at") val examCompletedAt: Long?,
    val level: Int = 0,
    @ColumnInfo(name = "subgroup_name") val subgroupName: String?       // NOT NULL via SQLAlchemy
)

@Entity(
    tableName = "grades",
    foreignKeys = [ForeignKey(
        entity = SubGroupEntity::class,
        parentColumns = ["name"],
        childColumns = ["subgroup_name"],
        onDelete = ForeignKey.NO_ACTION,
        onUpdate = ForeignKey.NO_ACTION
    )],
    indices = [Index("subgroup_name")]
)
data class GradeEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "subgroup_name") val subgroupName: String?,      // NOT NULL via SQLAlchemy
    val grade: Int?,
    val epoch: Int,
    val mode: String,
)