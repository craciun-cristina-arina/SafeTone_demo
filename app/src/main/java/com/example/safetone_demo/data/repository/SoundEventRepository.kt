package com.example.safetone_demo.data.repository
import kotlinx.coroutines.flow.Flow
import com.example.safetone_demo.data.local.dao.AudioEventDao
import com.example.safetone_demo.data.local.entity.AudioEventEntity
import com.example.safetone_demo.data.remote.MqttDataSource
import com.example.safetone_demo.wearable.WatchNotifier

class SoundEventRepository(
    private val eventDao: AudioEventDao,
    private val mqttDataSource: MqttDataSource,
    private val watchNotifier: WatchNotifier
) {

    // 1. Expose the local database to the UI
    fun getEventHistory(): Flow<List<AudioEventEntity>> {
        return eventDao.getAllEventsStream()
    }

    // 2. Start listening to the ESP32 and save events to Room
    // This would typically be called from your Foreground Service
    suspend fun startListeningToEdgeNode() {
        mqttDataSource.observeIncomingAlerts().collect { payload ->
            // We received an MQTT message!
            val newEvent = AudioEventEntity(
                soundType = payload.soundType,
                timestamp = System.currentTimeMillis(),
                confidenceScore = payload.confidence
            )
            // Save it to the database
            eventDao.insertEvent(newEvent)

            // NOTE: This is also exactly where you would trigger
            // the ping to the Pixel Watch!
            watchNotifier.sendAlertToWatch(payload.soundType)
        }
    }

    suspend fun acknowledgeEvent(eventId: Int) {
        // 1. Clear it from the database (so it vanishes from the phone UI)
        eventDao.markAsAcknowledged(eventId)

        // 2. Shoot the cancel signal to the watch!
        watchNotifier.sendAlertToWatch("CANCEL_ALERT")
    }
}