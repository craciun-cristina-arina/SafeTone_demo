package com.example.safetone_demo.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.safetone_demo.data.local.dao.AudioEventDao
import com.example.safetone_demo.data.local.entity.AudioEventEntity

// This annotation tells Room which entities belong to this database
@Database(entities = [AudioEventEntity::class], version = 1, exportSchema = false)
abstract class SoundDatabase : RoomDatabase() {

    // This tells Room to automatically generate the code for our DAO
    abstract fun audioEventDao(): AudioEventDao

}