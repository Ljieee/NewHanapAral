package com.example.hanaparalgroup.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val HanapAralColorScheme = lightColorScheme(
    primary              = Ink900,
    onPrimary            = White,
    primaryContainer     = Ink100,
    onPrimaryContainer   = Ink900,

    secondary            = Accent,
    onSecondary          = White,
    secondaryContainer   = AccentSoft,
    onSecondaryContainer = Ink900,

    tertiary             = Danger,
    onTertiary           = White,
    tertiaryContainer    = DangerLight,
    onTertiaryContainer  = Ink900,

    background           = Ink50,
    onBackground         = Ink900,

    surface              = White,
    onSurface            = Ink900,
    surfaceVariant       = Ink100,
    onSurfaceVariant     = Ink400,

    outline              = Ink200,
    outlineVariant       = Ink100,

    error                = Danger,
    onError              = White,
    errorContainer       = DangerLight,
    onErrorContainer     = Ink900,

    inverseSurface       = Ink900,
    inverseOnSurface     = White,
    inversePrimary       = Accent,
)

@Composable
fun HanapAralGroupTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = HanapAralColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Ink900.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = AppTypography,
        shapes      = AppShapes,
        content     = content
    )
}