package com.example.safetone_demo.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.safetone_demo.R
import com.example.safetone_demo.SafeToneApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class AlertListenerService : Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    // Using a constant prevents any typos between the builder and the channel creator!
    private val CHANNEL_ID = "SafeToneChannel"

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // 1. Build the persistent notification
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("SafeTone is Active")
            .setContentText("Listening for audio triggers...")
            .setSmallIcon(R.mipmap.ic_launcher) // Using your app's actual icon!
            .setOngoing(true)
            .build()

        // 2. Start the service (with Android 14 strict requirements)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(1, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)
        } else {
            startForeground(1, notification)
        }

        // 3. Grab your repository and start listening
        val repository = (application as SafeToneApp).repository
        serviceScope.launch {
            repository.startListeningToEdgeNode()
        }

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Background Listening Service",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
}