package com.example.safetone_demo

import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.safetone_demo.ui.components.SafeToneNavGraph
import com.example.safetone_demo.ui.dashboard.DashboardViewModel
import com.example.safetone_demo.ui.theme.SafeToneTheme
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Locale

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var lastAnnouncedEventId: Int = -1

    private val viewModelFactory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val repository = (application as SafeToneApp).repository
            return DashboardViewModel(repository) as T
        }
    }

    private val dashboardViewModel: DashboardViewModel by viewModels { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        tts = TextToSpeech(this, this)

        lifecycleScope.launch {
            dashboardViewModel.allEvents.collectLatest { events ->
                val latestEvent = events.firstOrNull()
                if (latestEvent != null && latestEvent.id != lastAnnouncedEventId) {

                    val timeDiff = System.currentTimeMillis() - latestEvent.timestamp
                    if (timeDiff < 5000) {
                        announceAlert(latestEvent.soundType)
                    }
                    lastAnnouncedEventId = latestEvent.id
                }
            }
        }

        setContent {
            var isDarkTheme by remember { mutableStateOf(false) }

            SafeToneTheme(darkTheme = isDarkTheme) {
                SafeToneNavGraph(
                    isDarkTheme = isDarkTheme,
                    onThemeChange = { newThemeValue -> isDarkTheme = newThemeValue },
                    dashboardViewModel = dashboardViewModel
                )
            }
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            updateTtsLanguage()
        }
    }

    private fun updateTtsLanguage() {
        val currentLang = AppCompatDelegate.getApplicationLocales().toLanguageTags()
        val isSystemRo = Locale.getDefault().language == "ro"

        val locale = if (currentLang.contains("ro") || (currentLang.isEmpty() && isSystemRo)) {
            Locale("ro", "RO")
        } else {
            Locale.US
        }
        tts?.language = locale
    }

    private fun announceAlert(soundType: String) {
        val prefs = getSharedPreferences("SafeTonePrefs", android.content.Context.MODE_PRIVATE)
        if (!prefs.getBoolean("tts_enabled", true)) return

        updateTtsLanguage()

        val currentLang = AppCompatDelegate.getApplicationLocales().toLanguageTags()
        val isSystemRo = Locale.getDefault().language == "ro"
        val isRo = currentLang.contains("ro") || (currentLang.isEmpty() && isSystemRo)

        val translatedSound = if (isRo) {
            when (soundType.trim().uppercase()) {
                "FIRE ALARM" -> "Alarmă de incendiu"
                "DOORBELL" -> "Sonerie"
                "BABY CRYING" -> "Bebeluș plângând"
                "DOG BARKING" -> "Lătrat de câine"
                else -> "Sunet necunoscut"
            }
        } else {
            soundType
        }

        val message = if (isRo) {
            "Atenție! $translatedSound"
        } else {
            "Attention! $translatedSound detected"
        }

        tts?.speak(message, TextToSpeech.QUEUE_FLUSH, null, "SafeTone_Alert")
    }

    override fun onDestroy() {
        tts?.stop()
        tts?.shutdown()
        super.onDestroy()
    }
}