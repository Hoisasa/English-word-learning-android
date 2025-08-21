package com.sharksempire.englishcards.ui.composables

import android.util.Log
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import androidx.room.ColumnInfo
import androidx.room.util.copy
import com.sharksempire.englishcards.dao.GroupsDao
import com.sharksempire.englishcards.ui.composables.screens.Display_subgroups
import com.sharksempire.englishcards.ui.composables.screens.ModeSelectScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import javax.inject.Inject

//var screenState by rememberSaveable { mutableStateOf("GroupsScreen") }
//var currentGroup by rememberSaveable { mutableStateOf("") }
//var currentSubGroup by rememberSaveable(stateSaver = groupSaver) {
//    mutableStateOf<GroupsWithProgressData?>(
//        null
//    )
//}
//var studyMode by rememberSaveable { mutableStateOf("") }
//val groups by rememberSaveable(stateSaver = groupsSaver) { mutableStateOf(mutableStateListOf()) }
//val lessonWords by rememberSaveable(stateSaver = wordDataSaver) {
//    mutableStateOf(
//        mutableStateListOf()
//    )
//}
//var grade by rememberSaveable { mutableIntStateOf(0) }
//var restartRequested by rememberSaveable { mutableStateOf(true) }
//var isLoading by rememberSaveable { mutableStateOf(true) }
//var endOfLesson by rememberSaveable { mutableStateOf(false) }
//val lessonMistakes by rememberSaveable(stateSaver = wordDataSaver) {
//    mutableStateOf(
//        mutableStateListOf()
//    )
//}
//var showFilter by rememberSaveable { mutableStateOf(true) }


sealed interface ScreenState {
    object Group: ScreenState
    object SubGroup: ScreenState
    object Repetition: ScreenState
    object ModeChoose: ScreenState
    object Lesson: ScreenState
    object Summary: ScreenState
}

sealed interface Item {
    val name: String
    val pos: String
    
    data class GroupsWithProgressData(
        override val name: String,
        @ColumnInfo(name = "total_words") val total: Int,
        @ColumnInfo(name = "learned_words") val learned: Int,
        override val pos: String
    ) : Item
    
    data class SpacedRepetitionWordsWithLevel(
        val level: Int,
        override val name: String,
        val words_amount: Int,
        override val pos: String
    ) : Item
}


sealed interface CurrentTarget {
    val showFilter: Boolean  // common property
    
    data class GroupTarget(
        override val showFilter: Boolean = true
    ): CurrentTarget
    
    data class SubGroupTarget(
        val target: String,
        override val showFilter: Boolean = false
    ) : CurrentTarget
    
    data class RepetitionTarget(
        override val showFilter: Boolean = true
    ) : CurrentTarget
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


@HiltViewModel
class MainActivityViewModel @Inject constructor(private val repo: DictionaryRepository) :
    ViewModel() {
    
    private val _internalStorageFlow = MutableStateFlow<GroupsViewState>(
        value = GroupsViewState.Loading
    )
    val uiState = _internalStorageFlow.asStateFlow()
    
    fun getGroups() = viewModelScope.launch{
        _internalStorageFlow.update { return@update GroupsViewState.Loading }
        repo.getGroups().onSuccess { groups ->
            _internalStorageFlow.update {
                return@update GroupsViewState.Success(
                    showFilter = GroupsFilter.Main.showFilter,
                    content = groups,
                )
            }
        }.onFailure { exception ->
            _internalStorageFlow.update {
                return@update GroupsViewState.Error(
                    message = exception.message ?: "Unknown error occurred"
                )
            }
        }
    }
    
    fun getSubgroups(target: String) = viewModelScope.launch{
        _internalStorageFlow.update { return@update GroupsViewState.Loading }
        repo.getSubgroups(target).onSuccess { groups ->
            _internalStorageFlow.update {
                return@update GroupsViewState.Success(
                    showFilter = GroupsFilter.Sub.showFilter,
                    content = groups,
                )
            }
        }.onFailure { exception ->
            _internalStorageFlow.update {
                return@update GroupsViewState.Error(
                    message = exception.message ?: "Unknown error occurred"
                )
            }
        }
    }
}

@Serializable
sealed interface Screen {
    @Serializable
    object Groups
    @Serializable
    data class SubGroups(val target: String)
    @Serializable
    data class Review(val target: String)
    @Serializable
    object Mode
    @Serializable
    object Lesson
    @Serializable
    object Summary
}

@Composable
fun MyEnglishApp(modifier: Modifier = Modifier) {
    
    
    val viewModel: MainActivityViewModel = hiltViewModel()
    val navController = rememberNavController()
    
    Surface(modifier) {
        NavHost(navController = navController, startDestination = Screen.Groups) {
            composable<Screen.Groups> {
                Display_groups(
                    { target ->
                        navController.navigate(route = Screen.SubGroups(target = target))
                    },
//                { target ->
//                    navController.navigate(route = Screen.Review(target = target))
//                    }
                )
            }
            composable<Screen.SubGroups> {
                val arguments = it.toRoute<Screen.SubGroups>()
                Display_subgroups(
                    onSubgroupSelected = {
                        navController.navigate(route = Screen.Mode)
                    },
                    target = arguments.target
                )
            }
            composable<Screen.Mode> {
                ModeSelectScreen(
                    onModeChosen = { navController.navigate(route = Screen.Lesson) }
                )
            }
            
            
        }
    }
}

//        when (screenState) {
//
//            "GroupsScreen" -> {
//                val screenStateButton: (String) -> Unit
//                val selectionArgs: String?
//                selectionArgs = null
//
//
//
//                LaunchedEffect(selectionArgs) {
//                    groups.clear()
//                    groups.addAll(groupsDao.queryGroupsWithProgressData())
//                }
//
//                screenStateButton = {
//                    currentGroup = it
//                    screenState = "SubGroupsScreen"
//                }
//
//                if (groups.isNotEmpty()) {
//                    if (groups.map {it.pos}.toSet().size > 1) {
//                        val allPOS = groups.map { it.pos }.toSet().toCollection(ArrayList())
//                        Display_groups({ screenState = "ReviewScreen" }, screenStateButton, groups, modifier, showFilter, allPOS)
//                    } else {
//                        Display_groups({ screenState = "ReviewScreen" }, screenStateButton, groups, modifier)
//                    }
//                }
//            }
//
            


//        when (screenState) {
//
//            "AudioDebugging" -> AudioDebugScreen()
//
//            "GroupsScreen", "SubGroupsScreen" -> {
//                val sql: String
//                val selectionArgs: String?
//                val screenStateButton: (String) -> Unit
//                var allPOS: ArrayList<String> = ArrayList()
//
//                if (screenState == "GroupsScreen") {
//                    sql =
//                        """
//                        SELECT
//                            main_groups.name AS name,
//                            COUNT(words.id) AS total_words,
//                            SUM(CASE WHEN words.weight = 1.0 THEN 1 ELSE 0 END) AS learned_words,
//                            main_groups.pos_name AS pos,
//                            MIN(CASE WHEN subgroups.level > 0 THEN subgroups.level ELSE 100 END) AS level
//                        FROM main_groups
//                        JOIN subgroups ON subgroups.main_group_id = main_groups.name
//                        JOIN words ON words.subgroup_name = subgroups.name
//                        GROUP BY main_groups.name
//                        ORDER BY learned_words DESC
//                        """.trimIndent()
//                    selectionArgs = null
//
//                    screenStateButton = {
//                        currentGroup = it
//                        screenState = "SubGroupsScreen"
//                    }
//
//
//                } else {
//                    sql =
//                        """
//                        SELECT
//                            subgroups.name AS name,
//                            COUNT(words.id) AS total_words,
//                            SUM(CASE WHEN words.weight = 1.0 THEN 1 ELSE 0 END) AS learned_words,
//                            pos.name AS pos,
//                            subgroups.level as level
//                        FROM subgroups
//                        JOIN main_groups ON subgroups.main_group_id = main_groups.name
//                        JOIN pos ON main_groups.pos_name = pos.name
//                        JOIN words ON words.subgroup_name = subgroups.name
//                        WHERE subgroups.main_group_id = ?
//                        GROUP BY subgroups.name, pos.name
//                        """.trimIndent()
//                    selectionArgs = currentGroup
//                    screenStateButton = { queryTarget: String ->
//                        currentSubGroup = groups.find { it.name == queryTarget }!!
//                        screenState = "ModeSelectScreen"
//                    }
//
//                    BackHandler { screenState = "GroupsScreen" }
//                }
//
//                LaunchedEffect(selectionArgs) {
//                    groups.clear()
//                    groups.addAll(queryOverDict(context, sql, selectionArgs))
//                    showFilter = screenState=="GroupsScreen"
//                }
//
//                if (groups.isNotEmpty()) {
//                    if (groups.map {it.pos}.toSet().size > 1) {
//                        val allPOS = groups.map { it.pos }.toSet().toCollection(ArrayList())
//                        Display_groups({ screenState = "ReviewScreen" }, screenStateButton, groups, modifier, showFilter, allPOS)
//                    } else {
//                        Display_groups({ screenState = "ReviewScreen" }, screenStateButton, groups, modifier)
//                    }
//                }
//
//            }
//
//            "ModeSelectScreen" -> {
//                // Words should not persist across different modes
//                lessonWords.clear()
//                restartRequested = true
//                isLoading = true
//
//
//                endOfLesson = false
//                ModeSelectScreen(
//                    {
//                        studyMode = it
//                        screenState = "LessonScreen"
//                    },
//                    currentSubGroup
//                )
//
//                BackHandler { screenState = "SubGroupsScreen" }
//            }
//
//            "LessonScreen" -> {
//                Log.d("LoadingLesson", "We entered lesson screen")
//
//
//                LaunchedEffect(restartRequested) {
//                    if (restartRequested) {
//                        isLoading = true
//                        Log.d("LoadingLesson", "load start")
//                        if (lessonWords.isNotEmpty()) { //then its not the first time we entered the lesson
//                            updateWeightsOfLessonList(lessonWords, context, currentSubGroup!!.name)
//                            Log.d("LoadingLesson", "Weight update")
//                        } else {
//                            lessonWords.addAll(queryWords(context, currentSubGroup!!.name))
//                            Log.d("LoadingLesson", "full requery")
//                        }
//                        if (studyMode == "Practice") {
//                            lessonWords.reduceLearnedWordsTo1()
//                        }
//                        if (studyMode != "Overview") {
//                            lessonWords.shuffle()
//                        } // to be replaced with smart shuffle
//                        Log.d("Shuffle", "Lesson ready: ${lessonWords.joinToString { it.word }}")
//                        lessonMistakes.clear()
//                        endOfLesson = false // only here if it's a clean fresh start
//                        restartRequested = false
//                        isLoading = false
//                        Log.d("LoadingLesson", "load end")
//                    }
//                }
//
//                if (isLoading) {
//                    Log.d("LoadingLesson", "We want list of words NOW")
//                } else {
//                    if (!endOfLesson) {
//                        StudyScreen(
//                            studyMode, lessonWords, lessonMistakes,
//                            {
//                                grade = it
//                                endOfLesson = true
//                            },
//                            { restartRequested = true }
//                        )
//                    } else {
//                        SummaryScreen(
//                            studyMode,
//                            grade,
//                            currentSubGroup!!.name,
//                            lessonMistakes,
//                            { restartRequested = true },
//                            { screenState = "ModeSelectScreen" }
//                        )
//                    }
//                }
//
//                BackHandler {
//                    screenState = "ModeSelectScreen"
//                }
//            }
//
//            "ReviewScreen" -> {
//                val sql =
//                    """SELECT
//                    subgroups.name AS name,
//                    COUNT(words.id) AS total_words,
//                    SUM(CASE WHEN words.weight = 1.0 THEN 1 ELSE 0 END) AS learned_words,
//                    pos.name AS pos,
//                    subgroups.level AS level
//                    FROM subgroups
//                            JOIN main_groups ON subgroups.main_group_id = main_groups.name
//                            JOIN pos ON main_groups.pos_name = pos.name
//                            JOIN words ON words.subgroup_name = subgroups.name
//                            WHERE subgroups.level > 0
//                    GROUP BY subgroups.name, pos.name
//                    """.trimIndent()
//
//                ReviewScreen(queryOverDict(context, sql), buttonFunction = { queryTarget: GroupsWithProgressData ->
//                    currentSubGroup = queryTarget
//                    screenState = "ModeSelectScreen"
//                })
//
//                BackHandler {
//                    screenState = "GroupsScreen"
//                }
//            }
//        }
