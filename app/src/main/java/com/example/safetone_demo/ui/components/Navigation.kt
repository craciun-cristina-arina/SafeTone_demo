package com.example.safetone_demo.ui.components

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.safetone_demo.SafeToneApp
import com.example.safetone_demo.ui.dashboard.DashboardScreen
import com.example.safetone_demo.ui.dashboard.DashboardViewModel
import com.example.safetone_demo.ui.eventlog.EventLogScreen
import com.example.safetone_demo.ui.settings.SettingsScreen
import kotlinx.coroutines.launch

@Composable
fun SafeToneNavGraph(
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit,
    dashboardViewModel: DashboardViewModel
) {
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val repository = (context.applicationContext as SafeToneApp).repository

    val sharedPrefs = context.getSharedPreferences("SafeTonePrefs", Context.MODE_PRIVATE)
    var isTtsEnabled by remember {
        mutableStateOf(sharedPrefs.getBoolean("tts_enabled", true))
    }

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
            val realEvents by dashboardViewModel.allEvents.collectAsState(initial = emptyList())

            EventLogScreen(
                events = realEvents,
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
                isTtsEnabled = isTtsEnabled,
                onTtsChange = { enabled ->
                    isTtsEnabled = enabled
                    sharedPrefs.edit().putBoolean("tts_enabled", enabled).apply()
                    scope.launch {
                        repository.updateWatchTts(enabled)
                    }
                },
                onLanguageChange = { lang ->
                    scope.launch {
                        repository.updateWatchLanguage(lang)
                    }
                },
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