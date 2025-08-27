package com.sharksempire.englishcards.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.sharksempire.englishcards.AppDatabase
import com.sharksempire.englishcards.dao.GroupsDao
import com.sharksempire.englishcards.dao.WordsDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {
    
    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE words ADD COLUMN level INTEGER NOT NULL DEFAULT 0")
            database.execSQL("ALTER TABLE words ADD COLUMN exam_completed_at INTEGER")
        }
    }
    
    val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE grades ADD COLUMN mode TEXT NOT NULL")
        }
    }
    
    
    @Provides
    @Singleton
    fun providesDatabaseClient(@ApplicationContext applicationContext: Context): AppDatabase{
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "dictionary.db"
        )
            .addMigrations(MIGRATION_1_2)
            .addMigrations(MIGRATION_2_3)
            .build()
        return db
    }
    
    @Provides
    @Singleton
    fun providesGroupsDao(db: AppDatabase): GroupsDao {
        return db.groupsDao()
    }
    
    @Provides
    @Singleton
    fun providesWordsDao(db: AppDatabase): WordsDao {
        return db.wordsDao()
    }
}