package com.example.hanaparalgroup.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val HanapAralColorScheme = lightColorScheme(
    primary          = Brand,
    onPrimary        = Surface,
    primaryContainer = BrandLight,
    onPrimaryContainer = Surface,

    secondary        = Action,
    onSecondary      = Surface,
    secondaryContainer = ActionLight,
    onSecondaryContainer = TextPrimary,

    tertiary         = Alert,
    onTertiary       = Surface,
    tertiaryContainer = AlertLight,
    onTertiaryContainer = Surface,

    background       = Background,
    onBackground     = TextPrimary,

    surface          = Surface,
    onSurface        = TextPrimary,
    surfaceVariant   = SurfaceAlt,
    onSurfaceVariant = TextSecondary,

    outline          = Divider,
    outlineVariant   = SurfaceAlt,

    error            = Alert,
    onError          = Surface,
    errorContainer   = AlertLight,
    onErrorContainer = TextPrimary,

    inverseSurface   = TextPrimary,
    inverseOnSurface = Surface,
    inversePrimary   = ActionLight,
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
            window.statusBarColor = Brand.toArgb()
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