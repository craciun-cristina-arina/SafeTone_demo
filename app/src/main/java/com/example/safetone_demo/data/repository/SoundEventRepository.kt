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

    fun getEventHistory(): Flow<List<AudioEventEntity>> {
        return eventDao.getAllEventsStream()
    }

    suspend fun startListeningToEdgeNode() {
        mqttDataSource.observeIncomingAlerts().collect { payload ->
            val newEvent = AudioEventEntity(
                soundType = payload.soundType,
                timestamp = System.currentTimeMillis(),
                confidenceScore = payload.confidence
            )
            eventDao.insertEvent(newEvent)
            watchNotifier.sendAlertToWatch(payload.soundType)
        }
    }

    suspend fun acknowledgeEvent(eventId: Int) {
        eventDao.markAsAcknowledged(eventId)
        watchNotifier.sendAlertToWatch("CANCEL_ALERT")
    }

    suspend fun triggerManualAlert(soundType: String) {
        val newEvent = AudioEventEntity(
            soundType = soundType,
            timestamp = System.currentTimeMillis(),
            confidenceScore = 0.99f
        )
        eventDao.insertEvent(newEvent)
        watchNotifier.sendAlertToWatch(soundType)
    }

    suspend fun updateWatchLanguage(lang: String) {
        watchNotifier.sendLanguageUpdate(lang)
    }

    suspend fun updateWatchTts(isEnabled: Boolean) {
        watchNotifier.sendTtsUpdate(isEnabled)
    }
}