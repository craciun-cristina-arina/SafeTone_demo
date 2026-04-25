package com.example.safetone_demo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.safetone_demo.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SafeToneHeader(
    onMenuClick: () -> Unit,
    onGoogleLoginClick: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val headerBg = if (isDark) Color(0xFF121212) else Color(0xFFF5F5F5)
    val contentColor = if (isDark) Color.White else Color.Black

    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = headerBg,
            titleContentColor = contentColor,
            navigationIconContentColor = contentColor
        ),
        title = {
            Text(
                text = "SAFETONE",
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp
            )
        },
        navigationIcon = {
            IconButton(onClick = {
                println("CLICK DETECTAT PE BUTON") // Verifică în Logcat (bara de jos din Android Studio)
                onMenuClick()
            }) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Open menu",
                    tint = contentColor
                )
            }
        },
        actions = {
            IconButton(
                onClick = onGoogleLoginClick,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.google),
                    contentDescription = "Login",
                    tint = Color.Unspecified
                )
            }
        }
    )
}