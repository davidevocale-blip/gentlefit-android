package com.voicetasker.app.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.voicetasker.app.ui.screen.calendar.CalendarScreen
import com.voicetasker.app.ui.screen.categories.CategoriesScreen
import com.voicetasker.app.ui.screen.home.HomeScreen
import com.voicetasker.app.ui.screen.notedetail.NoteDetailScreen
import com.voicetasker.app.ui.screen.record.RecordScreen
import com.voicetasker.app.ui.screen.settings.SettingsScreen

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Record : Screen("record")
    data object Calendar : Screen("calendar")
    data object NoteDetail : Screen("note/{noteId}") {
        fun createRoute(noteId: Long) = "note/$noteId"
    }
    data object Categories : Screen("categories")
    data object Settings : Screen("settings")
}

data class BottomNavItem(val screen: Screen, val label: String, val selectedIcon: ImageVector, val unselectedIcon: ImageVector)

val bottomNavItems = listOf(
    BottomNavItem(Screen.Home, "Home", Icons.Filled.Home, Icons.Outlined.Home),
    BottomNavItem(Screen.Calendar, "Calendario", Icons.Filled.CalendarMonth, Icons.Outlined.CalendarMonth),
    BottomNavItem(Screen.Categories, "Categorie", Icons.Filled.Category, Icons.Outlined.Category),
    BottomNavItem(Screen.Settings, "Impostazioni", Icons.Filled.Settings, Icons.Outlined.Settings)
)

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in bottomNavItems.map { it.screen.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(containerColor = MaterialTheme.colorScheme.surface, tonalElevation = 2.dp) {
                    bottomNavItems.forEach { item ->
                        val selected = currentRoute == item.screen.route
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(item.screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true; restoreState = true
                                }
                            },
                            icon = { Icon(if (selected) item.selectedIcon else item.unselectedIcon, item.label) },
                            label = { Text(item.label, style = MaterialTheme.typography.labelSmall) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(navController = navController, startDestination = Screen.Home.route, modifier = Modifier.padding(innerPadding)) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onNavigateToRecord = { navController.navigate(Screen.Record.route) },
                    onNavigateToNoteDetail = { id -> navController.navigate(Screen.NoteDetail.createRoute(id)) },
                    onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
                )
            }
            composable(Screen.Record.route) {
                RecordScreen(onNavigateBack = { navController.popBackStack() })
            }
            composable(Screen.Calendar.route) {
                CalendarScreen(onNavigateToNoteDetail = { id -> navController.navigate(Screen.NoteDetail.createRoute(id)) })
            }
            composable(
                route = Screen.NoteDetail.route,
                arguments = listOf(navArgument("noteId") { type = NavType.LongType })
            ) {
                NoteDetailScreen(onNavigateBack = { navController.popBackStack() })
            }
            composable(Screen.Categories.route) { CategoriesScreen() }
            composable(Screen.Settings.route) { SettingsScreen() }
        }
    }
}
