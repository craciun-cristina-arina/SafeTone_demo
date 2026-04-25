package com.example.safetone_demo

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import com.example.safetone_demo.ui.dashboard.DashboardScreen
import com.example.safetone_demo.ui.theme.SafeToneTheme
import com.example.safetone_demo.service.AlertListenerService
import com.example.safetone_demo.ui.components.SafeToneNavGraph
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val serviceIntent = Intent(this, AlertListenerService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }
        setContent {
            SafeToneTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    SafeToneNavGraph()
                }
            }
        }
    }
}