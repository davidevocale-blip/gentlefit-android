package com.voicetasker.app.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Purple40,
    onPrimary = Color.White,
    primaryContainer = Purple20,
    onPrimaryContainer = Purple80,
    secondary = Pink40,
    onSecondary = Color.White,
    secondaryContainer = Pink20,
    onSecondaryContainer = Pink80,
    tertiary = Mint40,
    onTertiary = Color.White,
    tertiaryContainer = Mint20,
    onTertiaryContainer = Mint80,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    outline = DarkOutline,
    error = ErrorRed,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    onPrimary = Color.White,
    primaryContainer = Purple80,
    onPrimaryContainer = Purple20,
    secondary = Pink40,
    onSecondary = Color.White,
    secondaryContainer = Pink80,
    onSecondaryContainer = Pink20,
    tertiary = Mint40,
    onTertiary = Color.White,
    tertiaryContainer = Mint80,
    onTertiaryContainer = Mint20,
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant,
    outline = LightOutline,
    error = ErrorRed,
    onError = Color.White
)

@Composable
fun VoiceTaskerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = VoiceTaskerTypography,
        shapes = VoiceTaskerShapes,
        content = content
    )
}
