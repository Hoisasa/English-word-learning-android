package com.sharksempire.englishcards.ui.composables

import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
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
    data class SubGroups(val target: String) {
        companion object {
            fun from(savedStateHandle: SavedStateHandle) =
                savedStateHandle.toRoute<SubGroups>()
        }
    }
    @Serializable
    object Review
    @Serializable
    data class Mode(val target: String)
    @Serializable
    data class Lesson(val mode: String)
    @Serializable
    object Summary
}

@Composable
fun MyEnglishApp(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    
    data class TopLevelRoute<T : Any>(val name: String, val route: T, val icon: ImageVector)
    
    val topLevelRoutes = listOf(
        TopLevelRoute("Learn words", Screen.Groups, Icons.AutoMirrored.Filled.List),
        TopLevelRoute("Spaced repetition", Screen.Review, Icons.Filled.Refresh)
    )
    
    Scaffold(
        bottomBar = {
            BottomNavigation {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                topLevelRoutes.forEach { topLevelRoute ->
                    BottomNavigationItem(
                        icon = {
                            Icon(
                                topLevelRoute.icon,
                                contentDescription = topLevelRoute.name
                            )
                        },
                        label = { Text(topLevelRoute.name) },
                        selected = currentDestination?.hierarchy?.any { it.hasRoute(topLevelRoute.route::class) } == true,
                        onClick = {
                            navController.navigate(topLevelRoute.route) {
                                // Pop up to the start destination of the graph to
                                // avoid building up a large stack of destinations
                                // on the back stack as users select items
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                // Avoid multiple copies of the same destination when
                                // reselecting the same item
                                launchSingleTop = true
                                // Restore state when reselecting a previously selected item
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Groups,
            Modifier.padding(innerPadding)
        ) {
            composable<Screen.Groups> {
                Display_groups(onGroupSelected = { target ->
                        navController.navigate(Screen.SubGroups(target))
                })
            }
            
            composable<Screen.Review> {
                Text("Spaced repetition")
            }
            
            composable<Screen.SubGroups> {
                Display_subgroups(
                    onSubgroupSelected = { target ->
                        navController.navigate(route = Screen.Mode(target = target))
                    }
                )
            }



            navigation(startDestination = "Mode", route = "LessonGraph") {
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

                composable<Screen.Lesson> { backStackEntry ->
                    val parentEntry = remember { navController.getBackStackEntry("LessonGraph") }
                    val lessonVM = hiltViewModel<LessonViewModel>(parentEntry)
                    val arguments = backStackEntry.toRoute<Screen.Lesson>()
                    val modesList = LessonViewState.Success.StudyMode::class.sealedSubclasses.mapNotNull { it.objectInstance }
                    val modesMap = modesList.associateBy { it.displayName }

                    StudyScreen(
                        onLessonFinished = { navController.navigate(route = Screen.Summary) },
                        mode = modesMap[arguments.mode]!!,
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

