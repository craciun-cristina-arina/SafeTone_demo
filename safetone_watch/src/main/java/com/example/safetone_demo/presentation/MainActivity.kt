package com.example.safetone_demo.presentation

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.KeyEvent
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Locale

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var pendingAlertToSpeak: String? = null

    private val closeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == "CLOSE_WATCH_UI") {
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ContextCompat.registerReceiver(
            this,
            closeReceiver,
            IntentFilter("CLOSE_WATCH_UI"),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )

        val incomingSoundType = intent.getStringExtra("ALERT_TYPE") ?: "ALERT"
        pendingAlertToSpeak = incomingSoundType

        tts = TextToSpeech(this, this)

        setContent {
            WatchAlertScreen(soundType = incomingSoundType)
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            updateTtsLanguage()
            pendingAlertToSpeak?.let {
                if (it != "ALERT") {
                    announceAlert(it)
                }
                pendingAlertToSpeak = null
            }
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
        val prefs = getSharedPreferences("SafeTonePrefs", Context.MODE_PRIVATE)
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
        try {
            unregisterReceiver(closeReceiver)
        } catch (e: Exception) {
        }
        super.onDestroy()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return if (keyCode == KeyEvent.KEYCODE_STEM_1 || keyCode == KeyEvent.KEYCODE_STEM_2) {
            sendAcknowledgeToPhone()
            finish()
            true
        } else {
            super.onKeyDown(keyCode, event)
        }
    }

    private fun sendAcknowledgeToPhone() {
        val nodeClient = Wearable.getNodeClient(this)
        val messageClient = Wearable.getMessageClient(this)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val nodes = nodeClient.connectedNodes.await()
                for (node in nodes) {
                    messageClient.sendMessage(node.id, "/safetone_ack", ByteArray(0)).await()
                }
            } catch (e: Exception) {
                Log.e("SafeToneWatch", "ACK Failed", e)
            }
        }
    }
}