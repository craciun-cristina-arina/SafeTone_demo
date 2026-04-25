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

        // Check if the message is coming from our specific SafeTone path
        if (messageEvent.path == "/safetone_alert") {

            val messageString = String(messageEvent.data)

            if (messageString == "CANCEL_ALERT") {
                // The phone is telling us the user already dismissed it on the phone screen
                val intent = Intent("CLOSE_WATCH_UI")
                sendBroadcast(intent)
            } else {
                Log.d("SafeToneWatch", "Alert Received: $messageString")

                // 1. It's a real alert! Vibrate...
                triggerAlertVibration()

                // 2. ...AND immediately launch the MainActivity to the screen!
                val intent = Intent(this, MainActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                }
                startActivity(intent)
            }
        }
    }

    private fun triggerAlertVibration() {
        // Get the vibration hardware
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = getSystemService(VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(VIBRATOR_SERVICE) as Vibrator
        }

        // Define a sharp, pulsing pattern: [Wait, Vibrate, Wait, Vibrate, Wait, Vibrate]
        val pattern = longArrayOf(0, 500, 200, 500, 200, 500)

        // Trigger it! (Removed the old API check to fix the yellow warning)
        val effect = VibrationEffect.createWaveform(pattern, -1)
        vibrator.vibrate(effect)
    }
}