package com.example.safetone_demo.presentation.service

import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService

class AlertReceiverService : WearableListenerService() {

    override fun onMessageReceived(messageEvent: MessageEvent) {
        super.onMessageReceived(messageEvent)

        // 1. Check if the message is coming from our specific SafeTone path
        if (messageEvent.path == "/safetone_alert") {

            // 2. Decode the sound type (e.g., "Fire Alarm")
            val soundType = String(messageEvent.data)
            Log.d("SafeToneWatch", "Alert Received: $soundType")

            // 3. Trigger the physical haptic vibration!
            triggerAlertVibration()
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
        // This pattern is: Vibrate 500ms, pause 200ms (repeats 3 times)
        val pattern = longArrayOf(0, 500, 200, 500, 200, 500)

        // Trigger it!
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val effect = VibrationEffect.createWaveform(pattern, -1) // -1 means don't repeat infinitely
            vibrator.vibrate(effect)
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(pattern, -1)
        }
    }
}