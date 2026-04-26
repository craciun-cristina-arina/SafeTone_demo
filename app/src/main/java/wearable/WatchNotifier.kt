package com.example.safetone_demo.wearable

import android.content.Context
import android.util.Log
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.tasks.await

class WatchNotifier(context: Context) {

    private val messageClient = Wearable.getMessageClient(context)
    private val dataClient = Wearable.getDataClient(context)
    private val nodeClient = Wearable.getNodeClient(context)

    private val ALERT_PATH = "/safetone_alert"

    suspend fun sendAlertToWatch(soundType: String) {
        try {
            val connectedNodes = nodeClient.connectedNodes.await()
            for (node in connectedNodes) {
                messageClient.sendMessage(
                    node.id,
                    ALERT_PATH,
                    soundType.toByteArray()
                ).await()
            }
        } catch (e: Exception) {
            Log.e("WatchNotifier", "Alert failed", e)
        }
    }

    suspend fun sendLanguageUpdate(languageTag: String) {
        try {
            val dataRequest = PutDataMapRequest.create("/safetone_lang").apply {
                dataMap.putString("lang", languageTag)
                dataMap.putLong("timestamp", System.currentTimeMillis())
            }.asPutDataRequest()

            dataRequest.setUrgent()
            dataClient.putDataItem(dataRequest).await()
            Log.d("WatchNotifier", "Language synced: $languageTag")
        } catch (e: Exception) {
            Log.e("WatchNotifier", "Sync failed", e)
        }
    }
}