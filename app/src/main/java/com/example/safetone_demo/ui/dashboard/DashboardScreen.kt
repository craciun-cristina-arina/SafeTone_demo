package com.example.safetone_demo.ui.dashboard

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.safetone_demo.ui.theme.SafeToneTheme

@Composable
fun DashboardScreen() {
    var isAlert by remember { mutableStateOf(false) }
    val colorScheme = MaterialTheme.colorScheme

    val animatedBgColor by animateColorAsState(
        targetValue = if (isAlert) colorScheme.error else colorScheme.background,
        animationSpec = tween(durationMillis = 500),
        label = "BgColorAnimation"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(animatedBgColor)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "SAFETONE",
            color = if (isAlert) Color.White else colorScheme.primary,
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 4.sp
        )

        ElevatedCard(
            modifier = Modifier.size(300.dp),
            shape = RoundedCornerShape(40.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = colorScheme.surface),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = if (isAlert) "🔔" else "🎧", fontSize = 80.sp)
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = if (isAlert) "ALARMĂ" else "LINIȘTE",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Black,
                    color = colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(20.dp))

                SoundWaveform(
                    modifier = Modifier.fillMaxWidth(0.7f).height(50.dp),
                    color = if (isAlert) colorScheme.error else colorScheme.primary,
                    isAnimating = !isAlert
                )
            }
        }

        Button(
            onClick = { isAlert = !isAlert },
            modifier = Modifier.fillMaxWidth().height(64.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isAlert) Color.White else colorScheme.primary
            )
        ) {
            Text(
                text = if (isAlert) "RESETEAZĂ" else "TESTEAZĂ SISTEMUL",
                color = if (isAlert) Color.Red else Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun SoundWaveform(modifier: Modifier, color: Color, isAnimating: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "WaveformTransition")

    val animations = (0 until 8).map { index ->
        infiniteTransition.animateFloat(
            initialValue = 0.2f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = keyframes {
                    // Am mărit durata totală la 1500ms pentru o mișcare mai relaxată
                    durationMillis = 1500

                    // Ajustăm punctele de control pentru fluiditate
                    0.2f at index * 100 // Început
                    1f at index * 100 + 400 // Vârful valului
                    0.2f at index * 100 + 800 // Revenire
                },
                repeatMode = RepeatMode.Restart
            ),
            label = "BarAnimation$index"
        )
    }

    Canvas(modifier = modifier) {
        // ... restul codului de desenare (Canvas) rămâne la fel
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
        DashboardScreen()
    }
}