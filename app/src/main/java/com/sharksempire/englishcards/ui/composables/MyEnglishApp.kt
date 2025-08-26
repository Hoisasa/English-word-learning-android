package com.sharksempire.englishcards.ui.composables

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import androidx.navigation.toRoute
import androidx.room.ColumnInfo
import com.sharksempire.englishcards.ui.composables.screens.Display_subgroups
import com.sharksempire.englishcards.ui.composables.screens.LessonViewState
import com.sharksempire.englishcards.ui.composables.screens.ModeSelectScreen
import com.sharksempire.englishcards.ui.composables.screens.StudyScreen
import com.sharksempire.englishcards.ui.composables.screens.SummaryScreen
import com.sharksempire.englishcards.viewmodels.LessonViewModel
import kotlinx.serialization.Serializable
import kotlin.reflect.typeOf


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







@Serializable
sealed interface Screen {
    @Serializable
    object Groups
    @Serializable
    data class SubGroups(val target: String)
    @Serializable
    data class Review(val target: String)
    @Serializable
    data class Mode(val target: String)
    @Serializable
    data class Lesson(val mode: LessonViewState.Success.StudyMode)
    @Serializable
    object Summary
    @Serializable
    object LessonGraph
}

@Composable
fun MyEnglishApp(modifier: Modifier = Modifier) {
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
                    onSubgroupSelected = { target ->
                        navController.navigate(route = Screen.Mode(target = target))
                    },
                    target = arguments.target
                )
            }
            
            navigation(startDestination = "Mode", route = "LessonGraph"){
                composable<Screen.Mode> { backStackEntry ->
                    val parentEntry = remember { navController.getBackStackEntry("LessonGraph") }
                    val lessonVM = hiltViewModel<LessonViewModel>(parentEntry)
                    val arguments = backStackEntry.toRoute<Screen.Mode>()
                    
                    ModeSelectScreen(
                        onModeChosen = { mode ->
                            navController.navigate(route = Screen.Lesson(mode))
                        },
                        target = arguments.target,
                        viewModel = lessonVM,
                    )
                }
                
                composable<Screen.Lesson>(
                    typeMap = mapOf(
                        typeOf<LessonViewState.Success.StudyMode>() to LessonViewState.Success.ModeNavType.ModeType
                    )
                ) { backStackEntry ->
                    val parentEntry = remember { navController.getBackStackEntry("LessonGraph") }
                    val lessonVM = hiltViewModel<LessonViewModel>(parentEntry)
                    val arguments = backStackEntry.toRoute<Screen.Lesson>()
                    
                    StudyScreen(
                        onLessonFinished = { navController.navigate(route = Screen.Summary) },
                        mode = arguments.mode,
                        viewModel = lessonVM,
                    )
                }
                
                composable<Screen.Summary> { backStackEntry ->
                    val parentEntry = remember { navController.getBackStackEntry("LessonGraph") }
                    val lessonVM = hiltViewModel<LessonViewModel>(parentEntry)
                    
                    SummaryScreen(
                        onSaveClicked = { navController.navigate(route = Screen.SubGroups) },
                        viewModel = lessonVM
                    )
                }
            }
        }
    }
}

//fun extendDB(context: Context) {
//    val path = context.getDatabasePath("dictionary.db").absolutePath
//    val db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READWRITE)
//
//    db.use {
//        it.execSQL("ALTER TABLE subgroups ADD COLUMN level INTEGER NOT NULL DEFAULT 0")
//        it.execSQL("UPDATE subgroups SET level = 1 WHERE exam_completed = 1")
//    }
//}

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

