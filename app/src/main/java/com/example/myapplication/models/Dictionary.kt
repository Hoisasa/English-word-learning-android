package com.example.myapplication.models

import androidx.room.*

@Entity(tableName = "pos")
data class POS(
    @PrimaryKey val name: String
)

@Entity(
    tableName = "groups",
    foreignKeys = [ForeignKey(
        entity = POS::class,
        parentColumns = ["name"],
        childColumns = ["pos_name"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("pos_name")]
)
data class Group(
    @PrimaryKey val name: String,
    @ColumnInfo(name = "pos_name") val posName: String
)

@Entity(
    tableName = "subgroups",
    foreignKeys = [ForeignKey(
        entity = Group::class,
        parentColumns = ["name"],
        childColumns = ["group_name"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("group_name")]
)
data class SubGroup(
    @PrimaryKey val name: String,
    @ColumnInfo(name = "group_name") val groupName: String
)

@Entity(
    tableName = "words",
    foreignKeys = [ForeignKey(
        entity = SubGroup::class,
        parentColumns = ["name"],
        childColumns = ["subgroup_name"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("subgroup_name")]
)
data class Word(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val word: String,
    val translation: String,
    val transcription: String = "",
    val weight: Float = 1.0f,
    @ColumnInfo(name = "subgroup_name") val subgroupName: String
)