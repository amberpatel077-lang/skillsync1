package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.R

val PlusJakartaSans = FontFamily(
    Font(R.font.plus_jakarta_sans, FontWeight.Normal),
    Font(R.font.plus_jakarta_sans, FontWeight.Medium),
    Font(R.font.plus_jakarta_sans, FontWeight.SemiBold),
    Font(R.font.plus_jakarta_sans, FontWeight.Bold),
    Font(R.font.plus_jakarta_sans, FontWeight.ExtraBold)
)

val Inter = FontFamily(
    Font(R.font.inter, FontWeight.Normal),
    Font(R.font.inter, FontWeight.Medium),
    Font(R.font.inter, FontWeight.SemiBold),
    Font(R.font.inter, FontWeight.Bold)
)

/**
 * Returns dynamic typography adjusted for Light vs Dark theme:
 * - In Dark Mode: Optical halation compensation with relaxed letter spacing (tracking)
 *   and calibrated font weights to avoid letter collision on deep dark surfaces.
 * - In Light Mode: Editorial, tighter tracking and high-contrast stroke definition.
 * - Text colors are automatically resolved via MaterialTheme.colorScheme (onSurface, onSurfaceVariant).
 */
fun getAppTypography(darkTheme: Boolean): Typography {
    return Typography(
        displayLarge = TextStyle(
            fontFamily = PlusJakartaSans,
            fontWeight = if (darkTheme) FontWeight.Bold else FontWeight.ExtraBold,
            fontSize = 48.sp,
            lineHeight = 56.sp,
            letterSpacing = if (darkTheme) 0.2.sp else (-0.5).sp
        ),
        displayMedium = TextStyle(
            fontFamily = PlusJakartaSans,
            fontWeight = if (darkTheme) FontWeight.Bold else FontWeight.ExtraBold,
            fontSize = 40.sp,
            lineHeight = 48.sp,
            letterSpacing = if (darkTheme) 0.15.sp else (-0.5).sp
        ),
        displaySmall = TextStyle(
            fontFamily = PlusJakartaSans,
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp,
            lineHeight = 40.sp,
            letterSpacing = if (darkTheme) 0.2.sp else (-0.25).sp
        ),
        headlineLarge = TextStyle(
            fontFamily = PlusJakartaSans,
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
            lineHeight = 36.sp,
            letterSpacing = if (darkTheme) 0.2.sp else (-0.25).sp
        ),
        headlineMedium = TextStyle(
            fontFamily = PlusJakartaSans,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            lineHeight = 32.sp,
            letterSpacing = if (darkTheme) 0.2.sp else (-0.15).sp
        ),
        headlineSmall = TextStyle(
            fontFamily = PlusJakartaSans,
            fontWeight = if (darkTheme) FontWeight.Bold else FontWeight.SemiBold,
            fontSize = 20.sp,
            lineHeight = 28.sp,
            letterSpacing = if (darkTheme) 0.25.sp else 0.sp
        ),
        titleLarge = TextStyle(
            fontFamily = PlusJakartaSans,
            fontWeight = if (darkTheme) FontWeight.Bold else FontWeight.SemiBold,
            fontSize = 18.sp,
            lineHeight = 26.sp,
            letterSpacing = if (darkTheme) 0.25.sp else 0.sp
        ),
        titleMedium = TextStyle(
            fontFamily = PlusJakartaSans,
            fontWeight = if (darkTheme) FontWeight.Bold else FontWeight.SemiBold,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            letterSpacing = if (darkTheme) 0.3.sp else 0.1.sp
        ),
        titleSmall = TextStyle(
            fontFamily = PlusJakartaSans,
            fontWeight = if (darkTheme) FontWeight.SemiBold else FontWeight.Medium,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = if (darkTheme) 0.3.sp else 0.1.sp
        ),
        bodyLarge = TextStyle(
            fontFamily = Inter,
            fontWeight = if (darkTheme) FontWeight.Medium else FontWeight.Normal,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            letterSpacing = if (darkTheme) 0.4.sp else 0.2.sp
        ),
        bodyMedium = TextStyle(
            fontFamily = Inter,
            fontWeight = if (darkTheme) FontWeight.Medium else FontWeight.Normal,
            fontSize = 14.sp,
            lineHeight = 21.sp,
            letterSpacing = if (darkTheme) 0.35.sp else 0.2.sp
        ),
        bodySmall = TextStyle(
            fontFamily = Inter,
            fontWeight = if (darkTheme) FontWeight.Medium else FontWeight.Normal,
            fontSize = 12.sp,
            lineHeight = 17.sp,
            letterSpacing = if (darkTheme) 0.4.sp else 0.25.sp
        ),
        labelLarge = TextStyle(
            fontFamily = PlusJakartaSans,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = if (darkTheme) 0.35.sp else 0.1.sp
        ),
        labelMedium = TextStyle(
            fontFamily = PlusJakartaSans,
            fontWeight = if (darkTheme) FontWeight.SemiBold else FontWeight.Medium,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            letterSpacing = if (darkTheme) 0.4.sp else 0.25.sp
        ),
        labelSmall = TextStyle(
            fontFamily = PlusJakartaSans,
            fontWeight = if (darkTheme) FontWeight.Bold else FontWeight.SemiBold,
            fontSize = 10.sp,
            lineHeight = 14.sp,
            letterSpacing = if (darkTheme) 0.65.sp else 0.5.sp
        )
    )
}

// Fallback default
val Typography = getAppTypography(darkTheme = false)
