package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Primary Sky Blue Palette
val SkyBluePrimary = Color(0xFF0284C7)      // Sky 600 - Main vibrant sky blue
val SkyBlueDark = Color(0xFF0369A1)         // Sky 700 - Deep sky blue for crisp high contrast text
val SkyBlueLight = Color(0xFF38BDF8)        // Sky 400 - Fresh sky accent
val SkyBluePale = Color(0xFFE0F2FE)         // Sky 100 - Soft sky background container
val SkyBlueUltraPale = Color(0xFFF0F9FF)    // Sky 50 - Background ambient sky tint
val SkyBlueBorder = Color(0xFFBAE6FD)       // Sky 200 - Elegant sky border stroke
val SkyBlueDeepNavy = Color(0xFF0C4A6E)     // Sky 900 - Deep contrast text / headings

// Primary Brand Palette (Mapped to Sky Blue Theme)
val BrandCyan = SkyBlueDark                 // Deep Sky Blue (high contrast, WCAG compliant)
val BrandIndigo = SkyBluePrimary            // Sky 600
val BrandBlue = SkyBluePrimary              // Sky 600
val BrandPurple = SkyBlueDark               // Sky 700
val BrandEmerald = Color(0xFF0D9488)        // Deep Teal 600 (harmonious accent)
val BrandAmber = Color(0xFFD97706)          // Amber 600
val BrandRose = Color(0xFFE11D48)           // Rose 600
val BrandPink = SkyBlueLight                // Sky 400

// Dark/Light Theme Surfaces (Sky Blue Theme)
val DarkBg = SkyBlueUltraPale
val DarkSurface = Color(0xFFFFFFFF)
val DarkSurfaceElevated = SkyBluePale
val DarkSurfaceCard = Color(0xFFFFFFFF)
val DarkBorder = SkyBlueBorder
val DarkTextPrimary = SkyBlueDeepNavy       // Deep Sky Navy 900 (sharp, high-contrast dark text)
val DarkTextSecondary = Color(0xFF1E293B)   // Slate 800 (very dark, highly legible)
val DarkTextTertiary = Color(0xFF334155)    // Slate 700 (dark readable)

val SurfaceCard = Color(0xFFFFFFFF)
val SurfaceDark = SkyBlueUltraPale
val CardStroke = SkyBlueBorder
val SuccessGreen = BrandEmerald

// Light Theme Typography Tokens (Sky Blue Theme with Dark-colored fonts)
val LightBg = SkyBlueUltraPale
val LightSurface = Color(0xFFFFFFFF)
val LightSurfaceElevated = SkyBluePale
val LightSurfaceCard = Color(0xFFFFFFFF)
val LightBorder = SkyBlueBorder
val LightTextPrimary = SkyBlueDeepNavy
val LightTextSecondary = Color(0xFF1E293B)
val LightTextTertiary = Color(0xFF334155)

// Compatibility fallbacks for starter template
val Purple80 = SkyBlueLight
val PurpleGrey80 = DarkTextSecondary
val Pink80 = BrandRose
val Purple40 = SkyBluePrimary
val PurpleGrey40 = DarkTextTertiary
val Pink40 = BrandRose
