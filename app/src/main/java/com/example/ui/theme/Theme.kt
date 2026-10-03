package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = MathCyan,
    onPrimary = Color(0xFF003549),
    primaryContainer = MathCyanContainer,
    onPrimaryContainer = Color(0xFFC3E8FF),
    secondary = MathGold,
    onSecondary = Color(0xFF432C00),
    secondaryContainer = MathGoldContainer,
    onSecondaryContainer = Color(0xFFFFDF9E),
    tertiary = MathEmerald,
    onTertiary = Color(0xFF003822),
    background = MathNavyDark,
    surface = MathSurfaceDark,
    surfaceVariant = MathSurfaceElevatedDark,
    onBackground = Color(0xFFF1F5F9),
    onSurface = Color(0xFFF8FAFC),
    onSurfaceVariant = Color(0xFF94A3B8),
    error = MathRose
)

private val LightColorScheme = lightColorScheme(
    primary = MathPrimaryLight,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFBAE6FD),
    onPrimaryContainer = Color(0xFF001F2A),
    secondary = Color(0xFFD97706),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFEF3C7),
    onSecondaryContainer = Color(0xFF451A03),
    tertiary = Color(0xFF059669),
    onTertiary = Color.White,
    background = MathNavyLight,
    surface = MathSurfaceLight,
    surfaceVariant = MathSurfaceElevatedLight,
    onBackground = Color(0xFF0F172A),
    onSurface = Color(0xFF0F172A),
    onSurfaceVariant = Color(0xFF475569),
    error = MathRose
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to sleek mathematical dark theme
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
