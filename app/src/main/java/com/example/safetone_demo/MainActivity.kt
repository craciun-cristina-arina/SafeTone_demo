package com.example.safetone_demo

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.safetone_demo.ui.components.SafeToneNavGraph
import com.example.safetone_demo.ui.dashboard.DashboardViewModel
import com.example.safetone_demo.ui.theme.SafeToneTheme

class MainActivity : AppCompatActivity() {

    private val viewModelFactory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val repository = (application as SafeToneApp).repository
            return DashboardViewModel(repository) as T
        }
    }

    private val dashboardViewModel: DashboardViewModel by viewModels { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            // 1. Remember the theme state! (Defaults to whatever the phone's system is set to)
            var isDarkTheme by remember { mutableStateOf(false) }

            // 2. Pass the dynamic theme into your Theme wrapper
            SafeToneTheme(darkTheme = isDarkTheme) {
                SafeToneNavGraph(
                    isDarkTheme = isDarkTheme,
                    // 3. When the settings screen clicks the toggle, this updates the whole app instantly!
                    onThemeChange = { newThemeValue -> isDarkTheme = newThemeValue },
                    dashboardViewModel = dashboardViewModel
                )
            }
        }
    }
}