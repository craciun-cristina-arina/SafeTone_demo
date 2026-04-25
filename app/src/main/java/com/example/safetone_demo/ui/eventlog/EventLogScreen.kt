package com.example.safetone_demo.ui.eventlog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.safetone_demo.data.local.entity.AudioEventEntity
import com.example.safetone_demo.ui.theme.SafeToneTheme
import com.example.safetone_demo.ui.components.SafeToneHeader
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun EventLogScreen(
    events: List<AudioEventEntity>,
    onNavigateToDashboard: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "SAFETONE MENU",
                    modifier = Modifier.padding(16.dp),
                    fontWeight = FontWeight.Black,
                    color = colorScheme.primary
                )
                HorizontalDivider()
                NavigationDrawerItem(
                    label = { Text("DASHBOARD") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onNavigateToDashboard()
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                NavigationDrawerItem(
                    label = { Text("EVENT LOGS") },
                    selected = true,
                    onClick = {
                        scope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                SafeToneHeader(
                    onMenuClick = {
                        scope.launch { drawerState.open() }
                    },
                    onGoogleLoginClick = { }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colorScheme.background)
                    .padding(paddingValues)
            ) {
                Text(
                    text = "EVENT LOG",
                    color = colorScheme.primary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 2.sp,
                    modifier = Modifier.padding(24.dp)
                )

                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(events) { event ->
                        EventCard(event = event, colorScheme = colorScheme)
                    }
                }
            }
        }
    }
}

@Composable
fun EventCard(event: AudioEventEntity, colorScheme: ColorScheme) {
    val soundTypeUpper = event.soundType.uppercase()

    val indicatorColor = when (soundTypeUpper) {
        "FIRE ALARM", "BABY CRYING" -> colorScheme.error
        "DOORBELL", "DOG BARKING" -> colorScheme.primary
        "UNKNOWN SOUND" -> colorScheme.tertiary
        else -> colorScheme.outline
    }

    val cardColor = when (soundTypeUpper) {
        "FIRE ALARM", "BABY CRYING" -> colorScheme.tertiaryContainer
        "DOORBELL", "DOG BARKING" -> colorScheme.surfaceVariant
        "UNKNOWN SOUND" -> colorScheme.errorContainer
        else -> colorScheme.surface
    }

    val textColor = when (soundTypeUpper) {
        "FIRE ALARM", "BABY CRYING" -> colorScheme.onTertiaryContainer
        "DOORBELL", "DOG BARKING" -> colorScheme.onSurfaceVariant
        "UNKNOWN SOUND" -> colorScheme.onErrorContainer
        else -> colorScheme.onSurface
    }

    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    val timeString = sdf.format(Date(event.timestamp))

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(indicatorColor, RoundedCornerShape(50))
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = event.soundType.uppercase(),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
                Text(
                    text = "Detected at $timeString • Confidence: ${(event.confidenceScore * 100).toInt()}%",
                    fontSize = 12.sp,
                    color = textColor.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun EventLogPreview() {
    val previewEvents = listOf(
        AudioEventEntity(1, "Fire Alarm", System.currentTimeMillis(), 0.98f),
        AudioEventEntity(2, "Doorbell", System.currentTimeMillis() - 3600000, 0.85f),
        AudioEventEntity(3, "Baby Crying", System.currentTimeMillis() - 7200000, 0.92f),
        AudioEventEntity(4, "Dog Barking", System.currentTimeMillis() - 10800000, 0.75f),
        AudioEventEntity(5, "Unknown Sound", System.currentTimeMillis() - 14400000, 0.60f)
    )

    SafeToneTheme {
        EventLogScreen(
            events = previewEvents,
            onNavigateToDashboard = { }
        )
    }
}