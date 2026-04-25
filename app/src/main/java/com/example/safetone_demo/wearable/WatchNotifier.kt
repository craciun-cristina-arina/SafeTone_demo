package com.example.safetone_demo.wearable

import android.content.Context
import android.util.Log
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.tasks.await

class WatchNotifier(context: Context) {

    // These clients handle the actual Bluetooth/Wi-Fi connection to the watch
    private val messageClient = Wearable.getMessageClient(context)
    private val nodeClient = Wearable.getNodeClient(context)

    // The unique "address" the watch will be listening to
    private val ALERT_PATH = "/safetone_alert"

    suspend fun sendAlertToWatch(soundType: String) {
        try {
            // 1. Find all watches currently connected to this phone
            val connectedNodes = nodeClient.connectedNodes.await()

            // 2. Send the message to every connected watch
            for (node in connectedNodes) {
                messageClient.sendMessage(
                    node.id,
                    ALERT_PATH,
                    soundType.toByteArray() // We send the sound name as bytes
                ).await()

                Log.d("WatchNotifier", "Sent $soundType alert to watch: ${node.displayName}")
            }
        } catch (e: Exception) {
            Log.e("WatchNotifier", "Failed to send alert to watch", e)
        }
    }
}

