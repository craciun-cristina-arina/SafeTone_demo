package com.example.safetone_demo

import android.app.Application
import androidx.room.Room
import com.example.safetone_demo.data.local.SoundDatabase
import com.example.safetone_demo.data.remote.FakeMqttDataSource
import com.example.safetone_demo.data.repository.SoundEventRepository
import com.example.safetone_demo.wearable.WatchNotifier

class SafeToneApp : Application() {

    lateinit var database: SoundDatabase private set
    lateinit var repository: SoundEventRepository private set

    lateinit var watchNotifier: WatchNotifier private set

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(
            this,
            SoundDatabase::class.java,
            "sound_sentinel_db"
        ).build()

        // 2. Create the Fake Data Source
        val myFakeDataSource = FakeMqttDataSource()

        // 3. Create the Watch Notifier
        watchNotifier = WatchNotifier(this)

        repository = SoundEventRepository(
            eventDao = database.audioEventDao(),
            mqttDataSource = myFakeDataSource,
            watchNotifier = watchNotifier
        )
    }
}