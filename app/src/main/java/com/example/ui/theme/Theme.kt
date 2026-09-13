package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

enum class AppThemeMode(val label: String) {
    SYSTEM("System Default"),
    DARK("Dark Mode"),
    AMOLED("AMOLED Pure Black"),
    LIGHT("Light Mode")
}

private val DarkColorScheme = darkColorScheme(
    primary = WalloraNeonCyan,
    onPrimary = Color.Black,
    primaryContainer = WalloraElectricViolet,
    onPrimaryContainer = Color.White,
    secondary = WalloraVividMagenta,
    onSecondary = Color.White,
    tertiary = WalloraGold,
    background = DarkBackground,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkTextSecondary
)

private val AmoledColorScheme = darkColorScheme(
    primary = WalloraNeonCyan,
    onPrimary = Color.Black,
    primaryContainer = WalloraElectricViolet,
    onPrimaryContainer = Color.White,
    secondary = WalloraVividMagenta,
    onSecondary = Color.White,
    tertiary = WalloraGold,
    background = AmoledBackground,
    onBackground = DarkTextPrimary,
    surface = AmoledSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = AmoledSurfaceVariant,
    onSurfaceVariant = DarkTextSecondary
)

private val LightColorScheme = lightColorScheme(
    primary = WalloraElectricViolet,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEDE9FE),
    onPrimaryContainer = Color(0xFF4C1D95),
    secondary = WalloraVividMagenta,
    onSecondary = Color.White,
    tertiary = WalloraEmerald,
    background = LightBackground,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightTextSecondary
)

@Composable
fun WalloraTheme(
    themeMode: AppThemeMode = AppThemeMode.DARK,
    content: @Composable () -> Unit
) {
    val isSystemDark = isSystemInDarkTheme()
    val colorScheme = when (themeMode) {
        AppThemeMode.SYSTEM -> if (isSystemDark) DarkColorScheme else LightColorScheme
        AppThemeMode.DARK -> DarkColorScheme
        AppThemeMode.AMOLED -> AmoledColorScheme
        AppThemeMode.LIGHT -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = WalloraTypography,
        content = content
    )
}
