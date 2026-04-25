package com.example.safetone_demo.presentation

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import androidx.core.content.ContextCompat

class MainActivity : ComponentActivity() {

    private val closeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == "CLOSE_WATCH_UI") {
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ContextCompat.registerReceiver(
            this,
            closeReceiver,
            IntentFilter("CLOSE_WATCH_UI"),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )

        val incomingSoundType = intent.getStringExtra("ALERT_TYPE") ?: "ALERT"

        setContent {
            WatchAlertScreen(soundType = incomingSoundType)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            unregisterReceiver(closeReceiver)
        } catch (e: Exception) {
            // Already unregistered
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        // Physical button check (Stem 1 is the top button on Pixel Watch 4)
        return if (keyCode == KeyEvent.KEYCODE_STEM_1 || keyCode == KeyEvent.KEYCODE_STEM_2) {
            Log.d("SafeToneWatch", "Physical button pressed - Acknowledging")
            sendAcknowledgeToPhone()
            finish()
            true
        } else {
            super.onKeyDown(keyCode, event)
        }
    }

    private fun sendAcknowledgeToPhone() {
        val nodeClient = Wearable.getNodeClient(this)
        val messageClient = Wearable.getMessageClient(this)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val nodes = nodeClient.connectedNodes.await()
                for (node in nodes) {
                    messageClient.sendMessage(node.id, "/safetone_ack", ByteArray(0)).await()
                }
            } catch (e: Exception) {
                Log.e("SafeToneWatch", "ACK Failed", e)
            }
        }
    }
}
