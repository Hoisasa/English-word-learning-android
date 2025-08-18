package com.sharksempire.englishcards

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.dict.db.GradeEntity
import com.example.dict.db.GroupEntity
import com.example.dict.db.POSEntity
import com.example.dict.db.SubGroupEntity
import com.example.dict.db.WordEntity
import com.sharksempire.englishcards.dao.GroupsDao

@Database(entities = [
    POSEntity::class,
    GroupEntity::class,
    SubGroupEntity::class,
    WordEntity::class,
    GradeEntity::class,
    ],
    version = 1,)
abstract class AppDatabase: RoomDatabase() {
    abstract fun groupsDao(): GroupsDao
}