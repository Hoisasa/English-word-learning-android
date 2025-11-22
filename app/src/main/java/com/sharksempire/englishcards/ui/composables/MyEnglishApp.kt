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
import androidx.compose.runtime.LaunchedEffect
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
import com.sharksempire.englishcards.viewmodels.ReviewLessonViewModel
import kotlinx.serialization.Serializable

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
    val level: Int,
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
    object ReviewGroups
    @Serializable
    data class ReviewLesson(val levelTarget: Int, val subgroupTarget: String?) {
        companion object {
            fun from(savedStateHandle: SavedStateHandle) =
                savedStateHandle.toRoute<ReviewLesson>()
        }
    }
    
    @Serializable
    object ReviewSummary
    @Serializable
    data class Mode(val target: String) {
        companion object {
            fun from(savedStateHandle: SavedStateHandle) =
                savedStateHandle.toRoute<Mode>()
        }
    }
    @Serializable
    object Lesson
    @Serializable
    object Summary
}

@Composable
fun MyEnglishApp(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    
    data class TopLevelRoute<T : Any>(val name: String, val route: T, val icon: ImageVector)
    
    val topLevelRoutes = listOf(
        TopLevelRoute("Learn words", Screen.Groups, Icons.AutoMirrored.Filled.List),
        TopLevelRoute("Spaced repetition", Screen.ReviewGroups, Icons.Filled.Refresh)
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
                        navController.navigate(route = Screen.SubGroups(target))
                })
            }

            composable<Screen.SubGroups> {
                Display_subgroups(
                    onSubgroupSelected = { target ->
                        navController.navigate(route = Screen.Mode(target))
                    }
                )
            }


            navigation(startDestination = "Mode", route = "LessonGraph") {
                composable<Screen.Mode> {
                    val parentEntry = remember { navController.getBackStackEntry("LessonGraph") }
                    val lessonVM = hiltViewModel<LessonViewModel>(parentEntry)


                    ModeSelectScreen(
                        onModeChosen = { mode ->
                            lessonVM.prepareLesson(mode)
                            navController.navigate(route = Screen.Lesson)
                        },
                        viewModel = lessonVM,
                    )
                }

                composable<Screen.Lesson> {
                    val parentEntry = remember { navController.getBackStackEntry("LessonGraph") }
                    val lessonVM = hiltViewModel<LessonViewModel>(parentEntry)

                    StudyScreen(
                        onLessonFinished = { navController.navigate(route = Screen.Summary) },
                        viewModel = lessonVM,
                    )
                }

                composable<Screen.Summary> {
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
            
            composable<Screen.ReviewGroups> {
                ReviewScreen( onReviewSelected = { levelTarget, subgroupTarget ->
                    navController.navigate(route = Screen.ReviewLesson(levelTarget, subgroupTarget))
                })
            }
            
            navigation(startDestination = "ReviewLesson", route = "ReviewGraph") {
                composable<Screen.ReviewLesson> {
                    val parentEntry = remember { navController.getBackStackEntry("ReviewGraph") }
                    val reviewVM = hiltViewModel<ReviewLessonViewModel>(parentEntry)
                    
                    StudyScreen(
                        onLessonFinished = { navController.navigate(route = Screen.ReviewSummary) },
                        viewModel = reviewVM,
                    )
                }
                
                composable<Screen.ReviewSummary> {
                    val parentEntry = remember { navController.getBackStackEntry("ReviewGraph") }
                    val reviewVM = hiltViewModel<ReviewLessonViewModel>(parentEntry)
                    
                    SummaryScreen(
                        onSaveClicked = {
                            navController.navigateUp()
                            navController.navigateUp()
                            navController.navigateUp()
                        },
                        viewModel = reviewVM
                    )
                }
            }
        }
    }
}
