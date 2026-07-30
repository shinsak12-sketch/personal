package com.taskflow.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.taskflow.ui.calendar.CalendarScreen
import com.taskflow.ui.dashboard.DashboardScreen
import com.taskflow.ui.tasks.TaskEditScreen
import com.taskflow.ui.tasks.TaskListScreen

@Composable
fun TaskFlowNavHost(initialTaskId: Long = -1L) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showBottomBar = currentRoute in TopLevelDestination.entries.map { it.route }

    // 알림을 눌러 진입한 경우 해당 할 일 편집 화면을 연다.
    LaunchedEffect(initialTaskId) {
        if (initialTaskId != -1L) {
            navController.navigate(Routes.taskEdit(initialTaskId))
        }
    }

    Scaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomBar,
                enter = slideInVertically { it },
                exit = slideOutVertically { it },
            ) {
                NavigationBar {
                    TopLevelDestination.entries.forEach { destination ->
                        val selected = backStackEntry?.destination?.hierarchy
                            ?.any { it.route == destination.route } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(destination.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(destination.icon, contentDescription = destination.label) },
                            label = { Text(destination.label) },
                        )
                    }
                }
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.DASHBOARD,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(Routes.DASHBOARD) {
                DashboardScreen(
                    onAddTask = { navController.navigate(Routes.taskEdit()) },
                    onOpenTask = { id -> navController.navigate(Routes.taskEdit(id)) },
                    onSeeAllTasks = {
                        navController.navigate(Routes.TASKS) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                )
            }
            composable(Routes.TASKS) {
                TaskListScreen(
                    onAddTask = { navController.navigate(Routes.taskEdit()) },
                    onOpenTask = { id -> navController.navigate(Routes.taskEdit(id)) },
                )
            }
            composable(Routes.CALENDAR) {
                CalendarScreen(
                    onAddTask = { date ->
                        navController.navigate(Routes.taskEdit())
                    },
                    onOpenTask = { id -> navController.navigate(Routes.taskEdit(id)) },
                )
            }
            composable(
                route = Routes.TASK_EDIT,
                arguments = listOf(
                    navArgument(Routes.TASK_EDIT_ARG) {
                        type = NavType.LongType
                        defaultValue = -1L
                    },
                ),
            ) {
                TaskEditScreen(onDone = { navController.popBackStack() })
            }
        }
    }
}
