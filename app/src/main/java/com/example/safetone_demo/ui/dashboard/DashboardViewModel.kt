package com.example.safetone_demo.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.safetone_demo.data.local.entity.AudioEventEntity
import com.example.safetone_demo.data.repository.SoundEventRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val repository: SoundEventRepository
) : ViewModel() {

    // Observe the database for the most recent unacknowledged event
    val latestEvent: StateFlow<AudioEventEntity?> = repository.getEventHistory()
        .map { events ->
            val absoluteNewestEvent = events.firstOrNull()
            // Only trigger an alert if the very newest event hasn't been acknowledged
            if (absoluteNewestEvent != null && !absoluteNewestEvent.isAcknowledged) {
                absoluteNewestEvent
            } else {
                null
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val allEvents: StateFlow<List<AudioEventEntity>> = repository.getEventHistory()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun dismissAlert(eventId: Int) {
        viewModelScope.launch {
            repository.acknowledgeEvent(eventId)
        }
    }

    // --- DEMO CYCLING LOGIC ---
    private val demoSounds = listOf("Fire Alarm", "Doorbell", "Baby Crying", "Dog Barking", "Unknown Sound")
    private var demoIndex = 0

    fun triggerNextDemoAlert() {
        viewModelScope.launch {
            val nextSound = demoSounds[demoIndex]
            repository.triggerManualAlert(nextSound)

            // Move to the next sound in the list (loops back to 0 at the end)
            demoIndex = (demoIndex + 1) % demoSounds.size
        }
    }
}