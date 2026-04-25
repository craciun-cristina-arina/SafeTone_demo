package com.example.safetone_demo.data.remote

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.random.Random

class FakeMqttDataSource : MqttDataSource {

    // CHANGE THIS TO SWITCH MODES!
    // true = Random background alerts. false = Manual button control.
    private val isAutoModeEnabled = false

    private val mockSounds = listOf("Doorbell", "Baby Crying", "Fire Alarm", "Dog Barking")

    override fun observeIncomingAlerts(): Flow<RawEsp32Payload> = flow {
        while (true) {
            if (isAutoModeEnabled) {
                delay(Random.nextLong(5000, 10000))
                val randomSound = mockSounds.random()
                val fakeConfidence = Random.nextFloat() * (1.0f - 0.7f) + 0.7f
                emit(RawEsp32Payload(soundType = randomSound, confidence = fakeConfidence))
            } else {
                // Sleeps quietly in the background without generating random alerts
                delay(Long.MAX_VALUE)
            }
        }
    }
}