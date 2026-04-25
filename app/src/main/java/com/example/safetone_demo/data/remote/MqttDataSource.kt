package com.example.safetone_demo.data.remote
import kotlinx.coroutines.flow.Flow

// A simple data class for the raw incoming payload
data class RawEsp32Payload(val soundType: String, val confidence: Float)

interface MqttDataSource {
    // Connects to the broker and emits a stream of detected sounds
    fun observeIncomingAlerts(): Flow<RawEsp32Payload>
}