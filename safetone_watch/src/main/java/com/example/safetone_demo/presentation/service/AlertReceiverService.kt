package com.example.safetone_demo.presentation.service

import android.content.Intent
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import com.example.safetone_demo.presentation.MainActivity
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService

class AlertReceiverService : WearableListenerService() {

    override fun onMessageReceived(messageEvent: MessageEvent) {
        super.onMessageReceived(messageEvent)

        val messageString = String(messageEvent.data)

        if (messageEvent.path == "/safetone_lang") {
            Log.d("SafeToneWatch", "Schimbare limbă primită: $messageString")
            val appLocale = androidx.core.os.LocaleListCompat.forLanguageTags(messageString)
            androidx.appcompat.app.AppCompatDelegate.setApplicationLocales(appLocale)
            return // Ne oprim aici, nu deschidem UI-ul
        }

        if (messageEvent.path == "/safetone_alert") {

            val messageString = String(messageEvent.data)

            if (messageString == "CANCEL_ALERT") {
                Log.d("SafeToneWatch", "Cancel received from phone! Closing UI...")

                // Add the specific package name so the security flag lets it through!
                val intent = Intent("CLOSE_WATCH_UI").apply {
                    setPackage(packageName)
                }
                sendBroadcast(intent)

            } else {
                Log.d("SafeToneWatch", "Alert Received: $messageString")

                // 1. Vibrate safely across all Android versions
                triggerAlertVibration()

                // 2. Launch UI and pack the data! (Rewritten to fix putExtra)
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                intent.putExtra("ALERT_TYPE", messageString)

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

        // THE FIX: We must check for API 26 (Android O) to support your API 25 minimum!
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val effect = VibrationEffect.createWaveform(pattern, -1)
            vibrator.vibrate(effect)
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(pattern, -1) // Uses the old vibration method for older watches
        }
    }
}