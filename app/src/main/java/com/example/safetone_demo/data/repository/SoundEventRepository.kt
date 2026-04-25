package com.example.safetone_demo.data.repository
import kotlinx.coroutines.flow.Flow
import com.example.safetone_demo.data.local.dao.AudioEventDao
import com.example.safetone_demo.data.local.entity.AudioEventEntity
import com.example.safetone_demo.data.remote.MqttDataSource

class SoundEventRepository(
    private val eventDao: AudioEventDao,
    private val mqttDataSource: MqttDataSource
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
        }
    }

    suspend fun acknowledgeEvent(eventId: Int) {
        eventDao.markAsAcknowledged(eventId)
    }
}