package com.example.safetone_demo.ui.components

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.safetone_demo.ui.dashboard.DashboardScreen
import com.example.safetone_demo.ui.eventlog.EventLogScreen
import com.example.safetone_demo.data.local.entity.AudioEventEntity

@Composable
fun SafeToneNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "dashboard"
    ) {
        composable("dashboard") {
            DashboardScreen(
                onNavigateToEvents = { navController.navigate("event_log") }
            )
        }
        composable("event_log") {
            val dummyEvents = listOf(
                AudioEventEntity(1, "Fire Alarm", System.currentTimeMillis(), 0.98f),
                AudioEventEntity(2, "Doorbell", System.currentTimeMillis() - 3600000, 0.85f)
            )

            // MODIFICAREA ESTE AICI:
            EventLogScreen(
                events = dummyEvents,
                onNavigateToDashboard = {
                    navController.navigate("dashboard") {
                        popUpTo("dashboard") { inclusive = true }
                    }
                }
            )
        }
    }
}