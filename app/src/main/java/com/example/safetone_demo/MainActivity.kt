package com.example.safetone_demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape // IMPORTUL LIPSĂ
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.safetone_demo.ui.theme.SafeToneTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SafeToneTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var isAlert by remember { mutableStateOf(false) }
                    var lastMessage by remember { mutableStateOf("Sistem activ...") }

                    SafeToneScreen(
                        isAlert = isAlert,
                        lastMessage = lastMessage,
                        onTestClick = { isAlert = !isAlert }
                    )
                }
            }
        }
    }
}

@Composable
fun SafeToneScreen(isAlert: Boolean, lastMessage: String, onTestClick: () -> Unit) {
    val colorScheme = MaterialTheme.colorScheme
    val backgroundColor = if (isAlert) colorScheme.error else colorScheme.background

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "SafeTone",
            color = if (isAlert) Color.White else colorScheme.primary,
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold
        )

        ElevatedCard(
            modifier = Modifier.size(280.dp),
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconContainer(isAlert)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = if (isAlert) "ALARMĂ" else "LINIȘTE",
                    color = colorScheme.onSurface,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Column {
            Text(
                text = "Status: $lastMessage",
                color = if (isAlert) Color.White.copy(alpha = 0.8f) else colorScheme.onBackground.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onTestClick,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isAlert) Color.White else colorScheme.primary
                )
            ) {
                Text(
                    text = if (isAlert) "RESETEAZĂ TESTUL" else "TESTEAZĂ ALERTĂ",
                    color = if (isAlert) Color.Red else Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun IconContainer(isAlert: Boolean) {
    Box(
        modifier = Modifier
            .size(150.dp)
            .background(Color.White.copy(alpha = 0.1f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(text = if (isAlert) "🔔" else "🎧", fontSize = 60.sp)
    }
}

@Preview(showBackground = true)
@Composable
fun SafeTonePreview() {
    SafeToneTheme {
        SafeToneScreen(isAlert = false, lastMessage = "Previzualizare", onTestClick = {})
    }
}