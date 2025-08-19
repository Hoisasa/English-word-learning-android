package com.sharksempire.englishcards.ui.composables

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.room.ColumnInfo
import com.sharksempire.englishcards.dao.GroupsDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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
    data class GroupsWithProgressData(
        val name: String,
        @ColumnInfo(name = "total_words") val total: Int,
        @ColumnInfo(name = "learned_words") val learned: Int,
        val pos: String) : Item
    data class SpacedRepetitionWordsWithLevel(
        val level: Int,
        val name: String,
        val words_amount: Int,
        val pos: String) : Item
}

data class AppState(
    val screen: ScreenState,
    val content: List<Item>,
    val currentTarget: String,
)


class DictionaryRepository @Inject constructor(private val groupsDao: GroupsDao){
    fun getGroups(): List<Item.GroupsWithProgressData> {
        return groupsDao.queryGroupsWithProgressData()
    }
    
    fun getSubgroups(mainGroup: String): List<Item.GroupsWithProgressData> {
        return groupsDao.querySubgroupsWithProgressData(mainGroup)
    }
    
    fun somethingelse(): List<Item.SpacedRepetitionWordsWithLevel> {
        return groupsDao.querySpacedRepetitionGroups()
    }
}


@HiltViewModel
class MainActivityViewModel @Inject constructor(private val repo: DictionaryRepository): ViewModel() {
    private val _appState = MutableStateFlow(AppState(
        screen = ScreenState.Group,
        content = repo.getGroups(),
        currentTarget = ""
    ))
    val appState: StateFlow<AppState> = _appState.asStateFlow()
    
    fun selectGroup(group: String) {
        _appState.update {
            return@update it.copy {
                screen = ScreenState.SubGroup
            }
        }
    }
    
}



@Composable
fun MyEnglishApp(modifier: Modifier = Modifier)  {
    

    
    Surface(modifier) {
        
        when (screenState) {
            
            "GroupsScreen" -> {
                val screenStateButton: (String) -> Unit
                val selectionArgs: String?
                selectionArgs = null
                
                
                
                LaunchedEffect(selectionArgs) {
                    groups.clear()
                    groups.addAll(groupsDao.queryGroupsWithProgressData())
                }
                
                screenStateButton = {
                    currentGroup = it
                    screenState = "SubGroupsScreen"
                }
                
                if (groups.isNotEmpty()) {
                    if (groups.map {it.pos}.toSet().size > 1) {
                        val allPOS = groups.map { it.pos }.toSet().toCollection(ArrayList())
                        Display_groups({ screenState = "ReviewScreen" }, screenStateButton, groups, modifier, showFilter, allPOS)
                    } else {
                        Display_groups({ screenState = "ReviewScreen" }, screenStateButton, groups, modifier)
                    }
                }
            }
            
            


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
        }
    }
}
