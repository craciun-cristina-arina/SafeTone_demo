package com.example.safetone_demo.presentation.service

import android.content.Intent
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.example.safetone_demo.presentation.MainActivity
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService

class AlertReceiverService : WearableListenerService() {

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        for (event in dataEvents) {
            if (event.type == DataEvent.TYPE_CHANGED && event.dataItem.uri.path == "/safetone_lang") {
                val dataMap = DataMapItem.fromDataItem(event.dataItem).dataMap
                val lang = dataMap.getString("lang") ?: "en"

                val appLocale = LocaleListCompat.forLanguageTags(lang)
                AppCompatDelegate.setApplicationLocales(appLocale)
            }
            if (event.type == com.google.android.gms.wearable.DataEvent.TYPE_CHANGED && event.dataItem.uri.path == "/safetone_tts") {
                val dataMap = com.google.android.gms.wearable.DataMapItem.fromDataItem(event.dataItem).dataMap
                val isEnabled = dataMap.getBoolean("tts_enabled", true)

                val prefs = getSharedPreferences("SafeTonePrefs", android.content.Context.MODE_PRIVATE)
                prefs.edit().putBoolean("tts_enabled", isEnabled).apply()
            }
        }
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        super.onMessageReceived(messageEvent)

        if (messageEvent.path == "/safetone_alert") {
            val messageString = String(messageEvent.data)

            if (messageString == "CANCEL_ALERT") {
                val intent = Intent("CLOSE_WATCH_UI").apply {
                    setPackage(packageName)
                }
                sendBroadcast(intent)
            } else {
                triggerAlertVibration()
                val intent = Intent(this, MainActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    putExtra("ALERT_TYPE", messageString)
                }
                startActivity(intent)
            }
        }
    }

    private fun triggerAlertVibration() {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = getSystemService(VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(VIBRATOR_SERVICE) as Vibrator
        }

        val pattern = longArrayOf(0, 500, 200, 500, 200, 500)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val effect = VibrationEffect.createWaveform(pattern, -1)
            vibrator.vibrate(effect)
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(pattern, -1)
        }
    }
}