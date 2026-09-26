package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = SkyBluePrimary,
    onPrimary = Color.White,
    primaryContainer = SkyBluePale,
    onPrimaryContainer = SkyBlueDeepNavy,
    secondary = SkyBlueLight,
    onSecondary = SkyBlueDeepNavy,
    secondaryContainer = SkyBluePale,
    onSecondaryContainer = SkyBlueDark,
    tertiary = BrandEmerald,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFCCFBF1),
    onTertiaryContainer = Color(0xFF115E59),
    background = SkyBlueUltraPale,
    onBackground = SkyBlueDeepNavy,
    surface = Color.White,
    onSurface = SkyBlueDeepNavy,
    surfaceVariant = SkyBluePale,
    onSurfaceVariant = Color(0xFF1E293B),
    outline = SkyBlueBorder,
    outlineVariant = Color(0xFFBAE6FD)
)

private val LightColorScheme = lightColorScheme(
    primary = SkyBluePrimary,
    onPrimary = Color.White,
    primaryContainer = SkyBluePale,
    onPrimaryContainer = SkyBlueDeepNavy,
    secondary = SkyBlueLight,
    onSecondary = SkyBlueDeepNavy,
    secondaryContainer = SkyBluePale,
    onSecondaryContainer = SkyBlueDark,
    tertiary = BrandEmerald,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFCCFBF1),
    onTertiaryContainer = Color(0xFF115E59),
    background = SkyBlueUltraPale,
    onBackground = SkyBlueDeepNavy,
    surface = Color.White,
    onSurface = SkyBlueDeepNavy,
    surfaceVariant = SkyBluePale,
    onSurfaceVariant = Color(0xFF1E293B),
    outline = SkyBlueBorder,
    outlineVariant = Color(0xFFBAE6FD)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false, // Default to clean, modern Light Theme
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
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
