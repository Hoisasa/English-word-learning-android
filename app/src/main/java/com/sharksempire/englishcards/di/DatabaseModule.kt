package com.sharksempire.englishcards.di

import android.content.Context
import androidx.room.Room
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
    
    @Provides
    @Singleton
    fun providesDatabaseClient(@ApplicationContext applicationContext: Context): AppDatabase{
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "dictionary.db"
        )
            .allowMainThreadQueries()
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