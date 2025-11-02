package com.sharksempire.englishcards.ui.composables

import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Scaffold
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
import com.sharksempire.englishcards.ui.composables.screens.ReviewScreen
import com.sharksempire.englishcards.ui.composables.screens.StudyScreen
import com.sharksempire.englishcards.ui.composables.screens.SummaryScreen
import com.sharksempire.englishcards.viewmodels.LessonViewModel
import kotlinx.serialization.Serializable


sealed interface ScreenState {
    object Group: ScreenState
    object SubGroup: ScreenState
    object Repetition: ScreenState
    object ModeChoose: ScreenState
    object Lesson: ScreenState
    object Summary: ScreenState
}

data class GroupsWithProgressData(
    val name: String,
    @ColumnInfo(name = "total_words") val total: Int,
    @ColumnInfo(name = "learned_words") val learned: Int,
    val pos: String,
)

data class SpacedRepetitionLevelsWithDateData(
    val name: String,
    val level: Int,
    @ColumnInfo(name = "total_words") val total: Int,
    @ColumnInfo(name = "due_words") val due: Int,
)

data class SpacedRepetitionLevelsWithDateDataGrouped(
    val name: String,
    @ColumnInfo(name = "total_words") val total: Int,
    @ColumnInfo(name = "due_words") val due: Int,
    val subgroup: String,
    val pos: String,
)


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
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
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
                ReviewScreen(
//                    onReviewLevelSelected = {
//
//                },
//                   onReviewGroupSelected = {
//
//
//                }
                )
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
                        onSaveClicked = {
                            navController.navigateUp()
                            navController.navigateUp()
                            navController.navigateUp()
                        },
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

