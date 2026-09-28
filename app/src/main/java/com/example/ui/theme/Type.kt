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
 * - In Dark Mode: Slightly increased font weights and relaxed letter spacing (tracking)
 *   to avoid optical thinning and halation on deep dark backgrounds.
 * - In Light Mode: Crisp, tight letter spacing for an editorial, modern daylight aesthetic.
 * - Text colors are handled dynamically via MaterialTheme.colorScheme and LocalContentColor.
 */
fun getAppTypography(darkTheme: Boolean): Typography {
    return Typography(
        displayLarge = TextStyle(
            fontFamily = PlusJakartaSans,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 48.sp,
            lineHeight = 56.sp,
            letterSpacing = if (darkTheme) 0.sp else (-0.5).sp
        ),
        displayMedium = TextStyle(
            fontFamily = PlusJakartaSans,
            fontWeight = FontWeight.Bold,
            fontSize = 40.sp,
            lineHeight = 48.sp,
            letterSpacing = if (darkTheme) 0.sp else (-0.5).sp
        ),
        displaySmall = TextStyle(
            fontFamily = PlusJakartaSans,
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp,
            lineHeight = 40.sp,
            letterSpacing = if (darkTheme) 0.1.sp else (-0.25).sp
        ),
        headlineLarge = TextStyle(
            fontFamily = PlusJakartaSans,
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
            lineHeight = 36.sp,
            letterSpacing = if (darkTheme) 0.15.sp else (-0.25).sp
        ),
        headlineMedium = TextStyle(
            fontFamily = PlusJakartaSans,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            lineHeight = 32.sp,
            letterSpacing = if (darkTheme) 0.1.sp else (-0.15).sp
        ),
        headlineSmall = TextStyle(
            fontFamily = PlusJakartaSans,
            fontWeight = if (darkTheme) FontWeight.Bold else FontWeight.SemiBold,
            fontSize = 20.sp,
            lineHeight = 28.sp,
            letterSpacing = if (darkTheme) 0.15.sp else 0.sp
        ),
        titleLarge = TextStyle(
            fontFamily = PlusJakartaSans,
            fontWeight = if (darkTheme) FontWeight.Bold else FontWeight.SemiBold,
            fontSize = 18.sp,
            lineHeight = 26.sp,
            letterSpacing = if (darkTheme) 0.2.sp else 0.sp
        ),
        titleMedium = TextStyle(
            fontFamily = PlusJakartaSans,
            fontWeight = if (darkTheme) FontWeight.Bold else FontWeight.SemiBold,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            letterSpacing = if (darkTheme) 0.25.sp else 0.1.sp
        ),
        titleSmall = TextStyle(
            fontFamily = PlusJakartaSans,
            fontWeight = if (darkTheme) FontWeight.SemiBold else FontWeight.Medium,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = if (darkTheme) 0.25.sp else 0.1.sp
        ),
        bodyLarge = TextStyle(
            fontFamily = Inter,
            fontWeight = if (darkTheme) FontWeight.Medium else FontWeight.Normal,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            letterSpacing = if (darkTheme) 0.4.sp else 0.25.sp
        ),
        bodyMedium = TextStyle(
            fontFamily = Inter,
            fontWeight = if (darkTheme) FontWeight.Medium else FontWeight.Normal,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = if (darkTheme) 0.35.sp else 0.25.sp
        ),
        bodySmall = TextStyle(
            fontFamily = Inter,
            fontWeight = if (darkTheme) FontWeight.Medium else FontWeight.Normal,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            letterSpacing = if (darkTheme) 0.5.sp else 0.4.sp
        ),
        labelLarge = TextStyle(
            fontFamily = PlusJakartaSans,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = if (darkTheme) 0.3.sp else 0.1.sp
        ),
        labelMedium = TextStyle(
            fontFamily = PlusJakartaSans,
            fontWeight = if (darkTheme) FontWeight.SemiBold else FontWeight.Medium,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            letterSpacing = if (darkTheme) 0.4.sp else 0.3.sp
        ),
        labelSmall = TextStyle(
            fontFamily = PlusJakartaSans,
            fontWeight = if (darkTheme) FontWeight.Bold else FontWeight.SemiBold,
            fontSize = 10.sp,
            lineHeight = 14.sp,
            letterSpacing = if (darkTheme) 0.6.sp else 0.5.sp
        )
    )
}

// Fallback default
val Typography = getAppTypography(darkTheme = false)
