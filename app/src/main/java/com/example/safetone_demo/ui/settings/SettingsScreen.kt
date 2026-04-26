package com.example.safetone_demo.ui.settings

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.LocaleListCompat
import com.example.safetone_demo.R
import com.example.safetone_demo.ui.components.SafeToneHeader
import com.example.safetone_demo.ui.theme.SafeToneTheme
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit,
    onLanguageChange: (String) -> Unit,
    onNavigateToDashboard: () -> Unit,
    onNavigateToEvents: () -> Unit
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
                    text = stringResource(R.string.menu_title),
                    modifier = Modifier.padding(16.dp),
                    fontWeight = FontWeight.Black,
                    color = colorScheme.primary
                )
                HorizontalDivider()
                NavigationDrawerItem(
                    label = { Text(stringResource(R.string.nav_dashboard)) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onNavigateToDashboard()
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
                    text = stringResource(R.string.settings_title),
                    color = colorScheme.primary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 2.sp,
                    modifier = Modifier.padding(24.dp)
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = stringResource(R.string.settings_theme),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = colorScheme.onSurfaceVariant
                            )

                            IconToggleButton(
                                checked = isDarkTheme,
                                onCheckedChange = onThemeChange
                            ) {
                                Icon(
                                    imageVector = if (isDarkTheme) Icons.Filled.DarkMode else Icons.Filled.WbSunny,
                                    contentDescription = null,
                                    tint = if (isDarkTheme) colorScheme.primary else colorScheme.tertiary
                                )
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.Language,
                                    contentDescription = null,
                                    tint = colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = stringResource(R.string.settings_language),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = colorScheme.onSurfaceVariant
                                )
                            }

                            Row {
                                TextButton(onClick = {
                                    val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags("en")
                                    AppCompatDelegate.setApplicationLocales(appLocale)
                                    onLanguageChange("en")
                                }) {
                                    Text(
                                        text = "EN",
                                        fontWeight = if (AppCompatDelegate.getApplicationLocales().toLanguageTags().contains("en")) FontWeight.ExtraBold else FontWeight.Normal
                                    )
                                }
                                TextButton(onClick = {
                                    val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags("ro")
                                    AppCompatDelegate.setApplicationLocales(appLocale)
                                    onLanguageChange("ro")
                                }) {
                                    Text(
                                        text = "RO",
                                        fontWeight = if (AppCompatDelegate.getApplicationLocales().toLanguageTags().contains("ro")) FontWeight.ExtraBold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SettingsPreview() {
    SafeToneTheme(darkTheme = false) {
        SettingsScreen(
            isDarkTheme = false,
            onThemeChange = {},
            onLanguageChange = {},
            onNavigateToDashboard = {},
            onNavigateToEvents = {}
        )
    }
}