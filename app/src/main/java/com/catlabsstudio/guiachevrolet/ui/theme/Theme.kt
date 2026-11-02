package com.catlabsstudio.guiachevrolet.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val GmGold = Color(0xFFD49A15)
val GmNavy = Color(0xFF0B2545)
val GmDarkSurface = Color(0xFF121820)

val SeverityAlertBg = Color(0xFFFFF8E1)
val SeverityAlertColor = Color(0xFFF57F17)

val SeverityMaintBg = Color(0xFFE3F2FD)
val SeverityMaintColor = Color(0xFF0277BD)

val SeverityCriticalBg = Color(0xFFFFEBEE)
val SeverityCriticalColor = Color(0xFFC62828)

private val LightColors = lightColorScheme(
    primary = GmNavy,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD6E3FF),
    onPrimaryContainer = Color(0xFF001B3D),
    secondary = GmGold,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFE08D),
    onSecondaryContainer = Color(0xFF241A00),
    background = Color(0xFFF8F9FA),
    surface = Color.White
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFA8C8FF),
    onPrimary = Color(0xFF003063),
    primaryContainer = Color(0xFF00468C),
    onPrimaryContainer = Color(0xFFD6E3FF),
    secondary = Color(0xFFF3C048),
    onSecondary = Color(0xFF3E2E00),
    secondaryContainer = Color(0xFF594400),
    onSecondaryContainer = Color(0xFFFFE08D),
    background = GmDarkSurface,
    surface = Color(0xFF1B232E)
)

@Composable
fun GuiaChevroletTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
