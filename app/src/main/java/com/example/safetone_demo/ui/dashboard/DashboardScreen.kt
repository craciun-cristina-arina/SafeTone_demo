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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import com.example.safetone_demo.ui.theme.SafeToneTheme
import com.example.safetone_demo.ui.components.SafeToneHeader
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import kotlinx.coroutines.launch
enum class SafeToneState {
    LISTENING, ALERT, CALM
}

@Composable
fun DashboardScreen(onNavigateToEvents: () -> Unit) {
    var systemState by remember { mutableStateOf(SafeToneState.LISTENING) }
    val colorScheme = MaterialTheme.colorScheme

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val targetBgColor = when (systemState) {
        SafeToneState.ALERT -> colorScheme.error
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
                    "SAFETONE MENU",
                    modifier = Modifier.padding(16.dp),
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.primary
                )
                NavigationDrawerItem(
                    label = { Text("Dashboard") },
                    selected = true,
                    onClick = {
                        scope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                NavigationDrawerItem(
                    label = { Text("Event Logs") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onNavigateToEvents()
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                SafeToneHeader(
                    onMenuClick = { },
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
                            SafeToneState.ALERT -> com.example.safetone_demo.R.drawable.alarm
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
                            SafeToneState.ALERT -> "SOUND DETECTED!"
                            SafeToneState.CALM -> "ENVIRONMENT IS QUIET"
                            SafeToneState.LISTENING -> "LISTENING..."
                        }

                        Text(
                            text = statusText,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Black,
                            color = colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        SoundWaveform(
                            modifier = Modifier.fillMaxWidth(0.8f).height(60.dp),
                            color = when (systemState) {
                                SafeToneState.ALERT -> colorScheme.error
                                SafeToneState.CALM -> Color.Gray.copy(alpha = 0.5f)
                                SafeToneState.LISTENING -> colorScheme.primary
                            },
                            isAnimating = (systemState == SafeToneState.LISTENING)
                        )
                    }
                }

                Button(
                    onClick = {
                        systemState = when (systemState) {
                            SafeToneState.LISTENING -> SafeToneState.ALERT
                            SafeToneState.ALERT -> SafeToneState.CALM
                            SafeToneState.CALM -> SafeToneState.LISTENING
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(64.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (systemState == SafeToneState.ALERT) Color.White else colorScheme.primary
                    )
                ) {
                    Text(
                        text = "SWITCH STATE",
                        color = if (systemState == SafeToneState.ALERT) Color.Red else Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun SoundWaveform(modifier: Modifier, color: Color, isAnimating: Boolean) {
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
            val h = if (isAnimating) animations[i].value * height else height * 0.3f
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

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DashboardPreview() {
    SafeToneTheme {
        DashboardScreen(onNavigateToEvents = { })
    }
}