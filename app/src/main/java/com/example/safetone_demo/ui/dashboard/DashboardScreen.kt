package com.example.safetone_demo.ui.dashboard

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.text.style.TextAlign
import com.example.safetone_demo.R
import com.example.safetone_demo.ui.theme.SafeToneTheme
import com.example.safetone_demo.ui.components.SafeToneHeader
import kotlinx.coroutines.launch

sealed class SafeToneState {
    object LISTENING : SafeToneState()
    data class ALERT(val soundType: String) : SafeToneState()
    object CALM : SafeToneState()
}

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onNavigateToEvents: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    // 1. Observe the REAL state from the ViewModel
    val latestEvent by viewModel.latestEvent.collectAsState()

    // 2. Map the real database event to your UI state
    val systemState = if (latestEvent != null) {
        SafeToneState.ALERT(latestEvent!!.soundType)
    } else {
        SafeToneState.LISTENING
    }

    val colorScheme = MaterialTheme.colorScheme
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val targetBgColor = when (systemState) {
        is SafeToneState.ALERT -> {
            when ((systemState as SafeToneState.ALERT).soundType.uppercase()) {
                "FIRE ALARM", "BABY CRYING" -> Color(0xFFEF5350)
                "DOORBELL", "DOG BARKING" -> Color(0xFF42A5F5)
                else -> Color(0xFFFFA726)
            }
        }
        SafeToneState.CALM -> Color(0xFF0D47A1)
        SafeToneState.LISTENING -> colorScheme.background
    }

    val animatedBgColor by animateColorAsState(
        targetValue = targetBgColor,
        animationSpec = tween(durationMillis = 500),
        label = ""
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.menu_title),
                    modifier = Modifier.padding(16.dp),
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.primary
                )
                NavigationDrawerItem(
                    label = { Text(stringResource(R.string.nav_dashboard)) },
                    selected = true,
                    onClick = {
                        scope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                NavigationDrawerItem(
                    label = { Text(stringResource(R.string.nav_events)) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onNavigateToEvents()
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                NavigationDrawerItem(
                    label = { Text(stringResource(R.string.nav_settings)) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onNavigateToSettings()
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                SafeToneHeader(
                    onMenuClick = { scope.launch { drawerState.open() }},
                    onGoogleLoginClick = { }
                )
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(animatedBgColor)
                    .padding(paddingValues)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {

                ElevatedCard(
                    modifier = Modifier.size(320.dp),
                    shape = RoundedCornerShape(40.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = colorScheme.surface),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val imageRes = when (systemState) {
                            is SafeToneState.ALERT -> {
                                when ((systemState as SafeToneState.ALERT).soundType.uppercase()) {
                                    "FIRE ALARM", "BABY CRYING", "DOORBELL", "DOG BARKING" -> com.example.safetone_demo.R.drawable.alarm
                                    else -> com.example.safetone_demo.R.drawable.question_mark
                                }
                            }
                            SafeToneState.CALM -> com.example.safetone_demo.R.drawable.zzz
                            SafeToneState.LISTENING -> com.example.safetone_demo.R.drawable.microphone
                        }
                        Image(
                            painter = painterResource(id = imageRes),
                            contentDescription = null,
                            modifier = Modifier.size(100.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        val statusText = when (systemState) {
                            is SafeToneState.ALERT -> {
                                val type = (systemState as SafeToneState.ALERT).soundType.uppercase()
                                val resId = when (type) {
                                    "FIRE ALARM" -> R.string.sound_fire_alarm
                                    "DOORBELL" -> R.string.sound_doorbell
                                    "BABY CRYING" -> R.string.sound_baby_crying
                                    "DOG BARKING" -> R.string.sound_dog_barking
                                    else -> R.string.sound_unknown
                                }
                                stringResource(resId).uppercase()
                            }
                            SafeToneState.CALM -> stringResource(R.string.dashboard_calm)
                            SafeToneState.LISTENING -> stringResource(R.string.dashboard_listening)
                        }

                        Text(
                            text = statusText,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Black,
                            color = colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        SoundWaveform(
                            modifier = Modifier.fillMaxWidth(0.8f).height(60.dp),
                            color = when (systemState) {
                                is SafeToneState.ALERT -> {
                                    when ((systemState as SafeToneState.ALERT).soundType.uppercase()) {
                                        "FIRE ALARM", "BABY CRYING" -> Color(0xFFEF5350)
                                        "DOORBELL", "DOG BARKING" -> Color(0xFF42A5F5)
                                        else -> MaterialTheme.colorScheme.tertiary
                                    }
                                }
                                SafeToneState.CALM -> Color.Gray.copy(alpha = 0.5f)
                                SafeToneState.LISTENING -> colorScheme.primary
                            },
                            isAnimating = (systemState == SafeToneState.LISTENING),
                            staticHeightFraction = when (systemState) {
                                is SafeToneState.ALERT -> {
                                    if ((systemState as SafeToneState.ALERT).soundType.uppercase() == "FIRE ALARM") 1f else 0.6f
                                }
                                else -> 0.3f
                            }
                        )
                    }
                }

                Button(
                    onClick = {
                        if (systemState is SafeToneState.ALERT) {
                            // If an alert is active, DISMISS IT
                            latestEvent?.let { event ->
                                viewModel.dismissAlert(event.id)
                            }
                        } else {
                            // If the system is calm, SIMULATE AN ALERT
                            viewModel.triggerNextDemoAlert()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(64.dp),
                    shape = RoundedCornerShape(20.dp),
                    enabled = true, // ALWAYS ENABLED!
                    colors = ButtonDefaults.buttonColors(
                        // White when active, Blue/Primary when Calm
                        containerColor = if (systemState is SafeToneState.ALERT) Color.White else colorScheme.primary
                    )
                ) {
                    Text(
                        text = if (systemState is SafeToneState.ALERT) "DISMISS ALERT" else "SIMULATE DEMO ALERT",
                        color = if (systemState is SafeToneState.ALERT) Color.Red else Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun SoundWaveform(modifier: Modifier, color: Color, isAnimating: Boolean, staticHeightFraction: Float) {
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val animations = (0 until 8).map { index ->
        infiniteTransition.animateFloat(
            initialValue = 0.2f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = keyframes {
                    durationMillis = 1500
                    0.2f at index * 100
                    1f at index * 100 + 400
                    0.2f at index * 100 + 800
                },
                repeatMode = RepeatMode.Restart
            ),
            label = ""
        )
    }

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val barWidth = 10.dp.toPx()
        val gap = 6.dp.toPx()
        val totalBarWidth = (8 * barWidth) + (7 * gap)
        var startX = (width - totalBarWidth) / 2

        for (i in 0 until 8) {
            val h = if (isAnimating) animations[i].value * height else height * staticHeightFraction
            drawRoundRect(
                color = color,
                topLeft = Offset(startX, (height - h) / 2),
                size = Size(barWidth, h),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx())
            )
            startX += barWidth + gap
        }
    }
}

