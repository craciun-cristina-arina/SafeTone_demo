package com.example.safetone_demo.ui.components

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.safetone_demo.ui.dashboard.DashboardScreen
import com.example.safetone_demo.ui.dashboard.DashboardViewModel
import com.example.safetone_demo.ui.eventlog.EventLogScreen
import com.example.safetone_demo.ui.settings.SettingsScreen
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@Composable
fun SafeToneNavGraph(
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit,
    dashboardViewModel: DashboardViewModel
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "dashboard"
    ) {
        composable("dashboard") {
            DashboardScreen(
                viewModel = dashboardViewModel,
                onNavigateToEvents = { navController.navigate("event_log") },
                onNavigateToSettings = { navController.navigate("settings") }
            )
        }
        composable("event_log") {
            // 1. Grab the REAL database history from the ViewModel
            val realEvents by dashboardViewModel.allEvents.collectAsState()

            EventLogScreen(
                events = realEvents, // 2. Pass the real data into the screen!
                onNavigateToDashboard = {
                    navController.navigate("dashboard") {
                        popUpTo("dashboard") { inclusive = true }
                    }
                },
                onNavigateToSettings = { navController.navigate("settings") }
            )
        }
        composable("settings") {
            SettingsScreen(
                isDarkTheme = isDarkTheme,
                onThemeChange = onThemeChange,
                onNavigateToDashboard = {
                    navController.navigate("dashboard") {
                        popUpTo("dashboard") { inclusive = true }
                    }
                },
                onNavigateToEvents = { navController.navigate("event_log") }
            )
        }
    }
}