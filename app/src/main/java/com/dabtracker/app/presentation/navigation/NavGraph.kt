package com.dabtracker.app.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.dabtracker.app.DabTrackerApplication
import com.dabtracker.app.presentation.screens.extract.AddEditExtractScreen
import com.dabtracker.app.presentation.screens.extract.ExtractListScreen
import com.dabtracker.app.presentation.screens.home.HomeScreen
import com.dabtracker.app.presentation.screens.session.LogSessionScreen
import com.dabtracker.app.presentation.screens.session.RateSessionScreen
import com.dabtracker.app.presentation.screens.session.SessionHistoryScreen
import com.dabtracker.app.presentation.screens.settings.SettingsScreen

data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

val bottomNavItems = listOf(
    BottomNavItem("Home", Icons.Default.Home, Screen.Home.route),
    BottomNavItem("Extracts", Icons.Default.Inventory2, Screen.ExtractList.route),
    BottomNavItem("Sessions", Icons.Default.List, Screen.SessionHistory.route),
    BottomNavItem("Settings", Icons.Default.Settings, Screen.Settings.route)
)

@Composable
fun DabTrackerNavGraph(app: DabTrackerApplication) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    app = app,
                    onNavigateToLogSession = { extractId ->
                        navController.navigate(Screen.LogSession.createRoute(extractId))
                    },
                    onNavigateToExtracts = {
                        navController.navigate(Screen.ExtractList.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onNavigateToSessions = {
                        navController.navigate(Screen.SessionHistory.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }

            composable(Screen.ExtractList.route) {
                ExtractListScreen(
                    app = app,
                    onNavigateToAdd = { navController.navigate(Screen.AddExtract.route) },
                    onNavigateToEdit = { extractId ->
                        navController.navigate(Screen.EditExtract.createRoute(extractId))
                    },
                    onNavigateToLogSession = { extractId ->
                        navController.navigate(Screen.LogSession.createRoute(extractId))
                    }
                )
            }

            composable(Screen.AddExtract.route) {
                AddEditExtractScreen(
                    app = app,
                    extractId = null,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.EditExtract.route,
                arguments = listOf(navArgument("extractId") { type = NavType.LongType })
            ) { backStackEntry ->
                val extractId = backStackEntry.arguments?.getLong("extractId")
                AddEditExtractScreen(
                    app = app,
                    extractId = extractId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.SessionHistory.route) {
                SessionHistoryScreen(
                    app = app,
                    onNavigateToLogSession = {
                        navController.navigate(Screen.LogSession.createRoute())
                    },
                    onNavigateToRateSession = { sessionId ->
                        navController.navigate(Screen.RateSession.createRoute(sessionId))
                    }
                )
            }

            composable(
                route = Screen.LogSession.route,
                arguments = listOf(
                    navArgument("extractId") {
                        type = NavType.LongType
                        defaultValue = -1L
                    }
                )
            ) { backStackEntry ->
                val extractId = backStackEntry.arguments?.getLong("extractId")
                    ?.takeIf { it != -1L }
                LogSessionScreen(
                    app = app,
                    preselectedExtractId = extractId,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToRate = { sessionId ->
                        navController.navigate(Screen.RateSession.createRoute(sessionId)) {
                            popUpTo(Screen.Home.route)
                        }
                    }
                )
            }

            composable(
                route = Screen.RateSession.route,
                arguments = listOf(navArgument("sessionId") { type = NavType.LongType })
            ) { backStackEntry ->
                val sessionId = backStackEntry.arguments?.getLong("sessionId") ?: return@composable
                RateSessionScreen(
                    app = app,
                    sessionId = sessionId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Settings.route) {
                SettingsScreen(app = app)
            }
        }
    }
}

@Composable
private fun BottomBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar {
        bottomNavItems.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                onClick = {
                    navController.navigate(item.route) {
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
