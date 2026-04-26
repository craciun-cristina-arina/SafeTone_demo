package com.example.safetone_demo.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material3.AppScaffold
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import com.example.safetone_demo.R // Verifică să fie importul corect de resurse
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.tooling.preview.devices.WearDevices
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.background

@Composable
fun WatchAlertScreen(soundType: String) {
    val soundTypeUpper = soundType.uppercase()

    val accentColor = when (soundTypeUpper) {
        "FIRE ALARM", "BABY CRYING" -> Color(0xFFEF5350)
        "DOORBELL", "DOG BARKING" -> Color(0xFF42A5F5)
        else -> Color(0xFFFFA726)
    }

    // MAPARE TRADUCERI
    val soundNameRes = when (soundTypeUpper) {
        "FIRE ALARM" -> R.string.sound_fire_alarm
        "DOORBELL" -> R.string.sound_doorbell
        "BABY CRYING" -> R.string.sound_baby_crying
        "DOG BARKING" -> R.string.sound_dog_barking
        else -> R.string.sound_unknown
    }

    AppScaffold {
        ScreenScaffold {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
                    .border(4.dp, accentColor, CircleShape)
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(40.dp)
                            .border(2.dp, accentColor, CircleShape)
                    ) {
                        Text(
                            text = "!",
                            color = accentColor,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        // AICI: Folosește resursa tradusă
                        text = stringResource(soundNameRes).uppercase(),
                        color = Color.White,
                        fontSize = 20.sp, // Am micșorat puțin să încapă textele lungi în RO
                        fontWeight = FontWeight.Black,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        // AICI: Folosește resursa tradusă
                        text = stringResource(R.string.watch_dismiss),
                        color = Color.LightGray,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Preview(
    device = WearDevices.SMALL_ROUND,
    showSystemUi = true,
    backgroundColor = 0xff000000,
    showBackground = true,
    name = "Fire Alarm Preview"
)
@Composable
fun WatchAlertPreview() {
    // Înlocuiește 'SafeToneTheme' cu numele temei tale de pe ceas dacă e diferit
    Box(modifier = Modifier.background(Color.Black)) {
        WatchAlertScreen(soundType = "Fire Alarm")
    }
}

@Preview(
    device = WearDevices.SMALL_ROUND,
    showSystemUi = true,
    name = "Doorbell Preview"
)
@Composable
fun WatchAlertDoorbellPreview() {
    Box(modifier = Modifier.background(Color.Black)) {
        WatchAlertScreen(soundType = "Doorbell")
    }
}