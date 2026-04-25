package com.example.safetone_demo.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "audio_events")
data class AudioEventEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val soundType: String,         // e.g., "Baby Crying", "Doorbell"
    val timestamp: Long,           // Unix timestamp
    val confidenceScore: Float,    // e.g., 0.95 (from your TFLite model)
    val isAcknowledged: Boolean = false // Did the user dismiss it on the watch?
)