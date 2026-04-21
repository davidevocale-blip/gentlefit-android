package com.gentlefit.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = Plum40,
    onPrimary = Color.White,
    primaryContainer = Plum80,
    onPrimaryContainer = Plum20,
    secondary = SageGreen40,
    onSecondary = Color.White,
    secondaryContainer = SageGreen80,
    onSecondaryContainer = SageGreen20,
    tertiary = Mauve40,
    onTertiary = Color.White,
    tertiaryContainer = Mauve80,
    onTertiaryContainer = Mauve20,
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant,
    outline = LightOutline,
    error = ErrorSoft,
    onError = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = Plum60,
    onPrimary = Plum10,
    primaryContainer = Plum30,
    onPrimaryContainer = Plum80,
    secondary = SageGreen60,
    onSecondary = SageGreen10,
    secondaryContainer = SageGreen30,
    onSecondaryContainer = SageGreen80,
    tertiary = Mauve60,
    onTertiary = Mauve10,
    tertiaryContainer = Mauve30,
    onTertiaryContainer = Mauve80,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    outline = DarkOutline,
    error = ErrorSoft,
    onError = Color.White
)

@Composable
fun GentleFitTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.setDecorFitsSystemWindows(window, false)
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = GentleFitTypography,
        shapes = GentleFitShapes,
        content = content
    )
}
