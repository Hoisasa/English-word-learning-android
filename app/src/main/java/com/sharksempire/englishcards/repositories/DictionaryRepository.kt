package com.sharksempire.englishcards.repositories

import com.sharksempire.englishcards.dao.GroupsDao
import com.sharksempire.englishcards.ui.composables.Item
import com.sharksempire.englishcards.ui.composables.QueryOperation
import javax.inject.Inject

class DictionaryRepository @Inject constructor(private val groupsDao: GroupsDao){
    fun getGroups(): QueryOperation<List<Item.GroupsWithProgressData>> {
        return safeQueryCall {
            groupsDao.queryGroupsWithProgressData()
        }
    }
    
    fun getSubgroups(mainGroup: String): QueryOperation<List<Item.GroupsWithProgressData>> {
        return safeQueryCall {
            groupsDao.querySubgroupsWithProgressData(mainGroup)
        }
    }
    
    fun somethingelse(): QueryOperation<List<Item.SpacedRepetitionWordsWithLevel>> {
        return safeQueryCall {
            groupsDao.querySpacedRepetitionGroups()
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