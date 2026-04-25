package com.example.safetone_demo.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import com.example.safetone_demo.data.local.entity.AudioEventEntity

@Dao
interface AudioEventDao {
    @Insert
    suspend fun insertEvent(event: AudioEventEntity)

    // A Flow automatically updates the UI when a new event is inserted!
    @Query("SELECT * FROM audio_events ORDER BY timestamp DESC")
    fun getAllEventsStream(): Flow<List<AudioEventEntity>>

    @Query("UPDATE audio_events SET isAcknowledged = 1 WHERE id = :eventId")
    suspend fun markAsAcknowledged(eventId: Int)
}