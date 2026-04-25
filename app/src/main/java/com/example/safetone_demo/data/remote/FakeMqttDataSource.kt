package com.example.safetone_demo.data.remote

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.random.Random

class FakeMqttDataSource : MqttDataSource {

    // A list of possible sounds your ESP32 might detect
    private val mockSounds = listOf("Doorbell", "Baby Crying", "Fire Alarm", "Dog Barking")

    override fun observeIncomingAlerts(): Flow<RawEsp32Payload> = flow {
        // This loop runs forever as long as the app is listening
        while (true) {
            // 1. Wait for a random amount of time (between 5 and 15 seconds)
            delay(Random.nextLong(5000, 15000))

            // 2. Pick a random sound and generate a fake confidence score (70% to 100%)
            val randomSound = mockSounds.random()
            val fakeConfidence = Random.nextFloat() * (1.0f - 0.7f) + 0.7f

            // 3. Emit the fake payload down the stream to your Repository!
            emit(RawEsp32Payload(soundType = randomSound, confidence = fakeConfidence))
        }
    }
}