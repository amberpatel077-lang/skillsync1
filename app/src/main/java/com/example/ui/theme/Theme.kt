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
    primary = Color(0xFF38BDF8),               // Sky 400 - Radiant sky blue for dark mode
    onPrimary = Color(0xFF082F49),              // Sky 950
    primaryContainer = Color(0xFF0C4A6E),      // Sky 900
    onPrimaryContainer = Color(0xFFE0F2FE),    // Sky 100
    secondary = Color(0xFF7DD3FC),             // Sky 300
    onSecondary = Color(0xFF082F49),
    secondaryContainer = Color(0xFF164E63),
    onSecondaryContainer = Color(0xFFCFFAFE),
    tertiary = Color(0xFF2DD4BF),              // Teal 400
    onTertiary = Color(0xFF003731),
    tertiaryContainer = Color(0xFF115E59),
    onTertiaryContainer = Color(0xFFCCFBF1),
    background = Color(0xFF0A0F1D),            // DEEP DARK NIGHT BACKGROUND
    onBackground = Color(0xFFF8FAFC),          // CRISP LIGHT SLATE 50 TEXT
    surface = Color(0xFF111827),               // DARK SURFACE SLATE 900
    onSurface = Color(0xFFF8FAFC),             // CRISP LIGHT SLATE 50 TEXT
    surfaceVariant = Color(0xFF1E293B),        // DARK CONTAINER SLATE 800
    onSurfaceVariant = Color(0xFFCBD5E1),      // HIGH CONTRAST SLATE 300 TEXT (> 7:1)
    outline = Color(0xFF475569),               // SLATE 600 BORDER
    outlineVariant = Color(0xFF334155),
    inverseSurface = Color(0xFFF1F5F9),
    inverseOnSurface = Color(0xFF0A0F1D),
    inversePrimary = SkyBluePrimary
)

private val LightColorScheme = lightColorScheme(
    primary = SkyBlueDark,                     // Sky 700 - Deep sky blue for crisp high contrast
    onPrimary = Color.White,
    primaryContainer = SkyBluePale,
    onPrimaryContainer = Color(0xFF0C4A6E),
    secondary = SkyBlueDark,
    onSecondary = Color.White,
    secondaryContainer = SkyBluePale,
    onSecondaryContainer = Color(0xFF0C4A6E),
    tertiary = BrandEmerald,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFCCFBF1),
    onTertiaryContainer = Color(0xFF115E59),
    background = Color(0xFFF8FAFC),            // Clean Slate 50 background
    onBackground = Color(0xFF0F172A),          // Deep Slate 900 text (> 12:1)
    surface = Color.White,
    onSurface = Color(0xFF0F172A),             // Deep Slate 900 text (> 12:1)
    surfaceVariant = Color(0xFFF1F5F9),        // Slate 100 card surface
    onSurfaceVariant = Color(0xFF334155),      // Slate 700 text (> 5.8:1 WCAG AA compliant)
    outline = Color(0xFF94A3B8),               // Slate 400
    outlineVariant = Color(0xFFCBD5E1)
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
        typography = getAppTypography(darkTheme),
        content = content
    )
}
