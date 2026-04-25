package com.example.safetone_demo.ui.components

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.safetone_demo.ui.dashboard.DashboardScreen
import com.example.safetone_demo.ui.dashboard.DashboardViewModel
import com.example.safetone_demo.ui.eventlog.EventLogScreen
import com.example.safetone_demo.ui.settings.SettingsScreen
import com.example.safetone_demo.data.local.entity.AudioEventEntity

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
            val dummyEvents = listOf(
                AudioEventEntity(1, "Fire Alarm", System.currentTimeMillis(), 0.98f),
                AudioEventEntity(2, "Doorbell", System.currentTimeMillis() - 3600000, 0.85f),
                AudioEventEntity(3, "Baby Crying", System.currentTimeMillis() - 7200000, 0.92f),
                AudioEventEntity(4, "Dog Barking", System.currentTimeMillis() - 10800000, 0.75f),
                AudioEventEntity(5, "Unknown Sound", System.currentTimeMillis() - 14400000, 0.60f)
            )

            EventLogScreen(
                events = dummyEvents,
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