package com.example.safetone_demo

import android.app.Application
import androidx.room.Room
import com.example.safetone_demo.data.local.SoundDatabase
import com.example.safetone_demo.data.remote.FakeMqttDataSource
import com.example.safetone_demo.data.repository.SoundEventRepository

class SafeToneApp : Application() {

    lateinit var database: SoundDatabase private set
    lateinit var repository: SoundEventRepository private set

    override fun onCreate() {
        super.onCreate()

        database = Room.databaseBuilder(
            this,
            SoundDatabase::class.java,
            "sound_sentinel_db"
        ).build()

        // THIS IS WHERE YOU PUT THE FAKE REQUEST!
        // 1. Create the fake source
        val myFakeDataSource = FakeMqttDataSource()

        // 2. Hand it to the repository. The repository doesn't know it's fake!
        repository = SoundEventRepository(
            eventDao = database.audioEventDao(),
            mqttDataSource = myFakeDataSource
        )
    }
}