package com.example.ivalid_compose.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = RedPrimary,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    secondary = RedSecondary,
    background = BackgroundLight,
    surface = SurfaceLight,
    onBackground = OnBackgroundLight,
    onSurface = OnBackgroundLight,
    outline = OutlineLight
)

private val DarkColors = darkColorScheme(
    primary = RedPrimary,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    secondary = RedSecondary,
    background = BackgroundDark,
    surface = SurfaceDark,
    onBackground = OnBackgroundDark,
    onSurface = OnBackgroundDark,
    outline = OutlineDark
)

@Composable
fun AppTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = AppTypography,
        content = content
    )
}