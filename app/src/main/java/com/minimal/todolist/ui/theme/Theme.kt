package com.minimal.todolist.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColors = lightColorScheme(
    primary = IosBlack,
    onPrimary = IosWhite,
    background = IosWhite,
    onBackground = IosBlack,
    surface = IosWhite,
    onSurface = IosBlack,
    surfaceVariant = IosLightGray,
    onSurfaceVariant = IosGray,
    outline = IosSeparator,
    error = IosRed
)

private val DarkColors = darkColorScheme(
    primary = IosWhite,
    onPrimary = IosBlack,
    background = IosBlack,
    onBackground = IosWhite,
    surface = IosBlack,
    onSurface = IosWhite,
    surfaceVariant = Color2(),
    onSurfaceVariant = IosGray,
    outline = Color2(),
    error = IosRed
)

private fun Color2() = androidx.compose.ui.graphics.Color(0xFF1C1C1E)

@Composable
fun TodoListTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors
    val view = LocalView.current
    if (!view.isInEditMode) {
        val context = view.context
        val window = (context as? android.app.Activity)?.window
        if (window != null) {
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = TodoTypography,
        content = content
    )
}
