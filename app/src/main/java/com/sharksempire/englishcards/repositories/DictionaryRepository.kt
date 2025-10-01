package com.sharksempire.englishcards.repositories

import com.sharksempire.englishcards.dao.GroupsDao
import com.sharksempire.englishcards.dao.WordData
import com.sharksempire.englishcards.dao.WordWeight
import com.sharksempire.englishcards.dao.WordsDao
import com.sharksempire.englishcards.ui.composables.Item
import javax.inject.Inject

class DictionaryRepository @Inject constructor(
    private val groupsDao: GroupsDao,
    private val wordsDao: WordsDao
){
    suspend fun getGroups(): QueryOperation<List<Item.GroupsWithProgressData>> {
        return safeQueryCall {
            groupsDao.queryGroupsWithProgressData()
        }
    }
    
    suspend fun getSubgroups(mainGroup: String): QueryOperation<List<Item.GroupsWithProgressData>> {
        return safeQueryCall {
            groupsDao.querySubgroupsWithProgressData(mainGroup)
        }
    }
    
    suspend fun somethingelse(): QueryOperation<List<Item.SpacedRepetitionWordsWithLevel>> {
        return safeQueryCall {
            groupsDao.querySpacedRepetitionGroups()
        }
    }
    
    suspend fun getWords(subGroup: String): QueryOperation<List<WordData>> {
        return safeQueryCall {
            wordsDao.queryWords(subGroup = subGroup)
        }
    }
    
    suspend fun updateWeight(wordId: Int, mark: Float): QueryOperation<Unit> {
        return safeQueryCall {
            wordsDao.queryUpdateWeight(wordId = wordId, mark = mark)
        }
    }
    
    suspend fun getNewWeights(subGroup: String): QueryOperation<Map<Int, Float>> {
        return safeQueryCall {
            wordsDao.queryNewWeights(subGroup = subGroup).associate { it.id to it.weight }
        }
    }
    
    suspend fun setExamCompleted(subGroup: String): QueryOperation<Unit> {
        return safeQueryCall {
            wordsDao.markExamCompleted(subGroup = subGroup)
        }
    }
    
    private inline fun <T> safeQueryCall(apiCall: () -> T): QueryOperation<T> {
        return try {
            QueryOperation.Success(data = apiCall())
        } catch (e: Exception) {
            QueryOperation.Failure(exception = e)
        }
    }
}

sealed interface QueryOperation<T> {
    data class Success<T>(val data: T): QueryOperation<T>
    data class Failure<T>(val exception: Exception): QueryOperation<T>
    
    fun onSuccess(block: (T) -> Unit): QueryOperation<T> {
        if (this is Success) block(data)
        return this
    }
    
    fun onFailure(block: (Exception) -> Unit): QueryOperation<T> {
        if (this is Failure) block(exception)
        return this
    }
}