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
val DarkThemeBg = Color(0xFF0A0F1D)         // Deep dark night background
val DarkThemeSurface = Color(0xFF111827)    // Elevated card surface (Slate 900)
val DarkThemeSurfaceVariant = Color(0xFF1E293B) // Dark container (Slate 800)
val DarkThemeOutline = Color(0xFF334155)    // Dark border (Slate 700)

val DarkBg = Color(0xFF0A0F1D)
val DarkSurface = Color(0xFF111827)
val DarkSurfaceElevated = Color(0xFF1E293B)
val DarkSurfaceCard = Color(0xFF111827)
val DarkBorder = Color(0xFF334155)
val DarkTextPrimary = Color(0xFFF8FAFC)     // Slate 50 - Crisp high contrast white
val DarkTextSecondary = Color(0xFF94A3B8)   // Slate 400 - Highly legible silver
val DarkTextTertiary = Color(0xFF64748B)    // Slate 500 - Muted secondary

val SurfaceCard = Color(0xFFFFFFFF)
val SurfaceDark = Color(0xFF0A0F1D)
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

// Strict Semantic Status System (WCAG AA Compliant & Dark-Theme Adaptive)
data class StatusColors(
    val text: Color,
    val background: Color,
    val border: Color
)

object SemanticStatus {
    // 🔵 Informational (Category badges, general info, discovery filters)
    val Info = Color(0xFF0284C7)         // Sky 600
    val InfoBg = Color(0xFFE0F2FE)       // Sky 100
    val InfoDark = Color(0xFF38BDF8)     // Sky 400
    val InfoDarkBg = Color(0xFF0C4A6E)   // Sky 900
    val InfoBorder = Color(0xFFBAE6FD)

    // 🟢 Active / Verified / Success (open now, verified, available, accepted)
    val Success = Color(0xFF16A34A)      // Green 600
    val SuccessBg = Color(0xFFDCFCE7)    // Green 100
    val SuccessDark = Color(0xFF4ADE80)  // Green 400
    val SuccessDarkBg = Color(0xFF14532D)// Green 900
    val SuccessBorder = Color(0xFF86EFAC)

    // 🟠 Deadline / Pending (closing soon, under review, waiting, competition countdown)
    val Pending = Color(0xFFD97706)      // Amber 600
    val PendingBg = Color(0xFFFEF3C7)    // Amber 100
    val PendingDark = Color(0xFFFBBF24)  // Amber 400
    val PendingDarkBg = Color(0xFF78350F)// Amber 900
    val PendingBorder = Color(0xFFFDE68A)

    // 🔴 Error / Critical (urgent deadline, expired, rejected)
    val Critical = Color(0xFFDC2626)     // Red 600
    val CriticalBg = Color(0xFFFEE2E2)   // Red 100
    val CriticalDark = Color(0xFFF87171) // Red 400
    val CriticalDarkBg = Color(0xFF7F1D1D)// Red 900
    val CriticalBorder = Color(0xFFFECACA)

    fun resolve(type: StatusType, isDark: Boolean): StatusColors {
        return when (type) {
            StatusType.INFO -> if (isDark) StatusColors(InfoDark, InfoDarkBg, Color(0xFF0284C7)) else StatusColors(Info, InfoBg, InfoBorder)
            StatusType.SUCCESS -> if (isDark) StatusColors(SuccessDark, SuccessDarkBg, Color(0xFF16A34A)) else StatusColors(Success, SuccessBg, SuccessBorder)
            StatusType.PENDING -> if (isDark) StatusColors(PendingDark, PendingDarkBg, Color(0xFFD97706)) else StatusColors(Pending, PendingBg, PendingBorder)
            StatusType.CRITICAL -> if (isDark) StatusColors(CriticalDark, CriticalDarkBg, Color(0xFFDC2626)) else StatusColors(Critical, CriticalBg, CriticalBorder)
        }
    }
}

enum class StatusType {
    INFO,       // 🔵 Informational (Category, mode, general tags)
    SUCCESS,    // 🟢 Active / verified / success (open now, verified, available, accepted)
    PENDING,    // 🟠 Deadline / pending (closing soon, under review, waiting, competition countdown)
    CRITICAL    // 🔴 Error / critical (urgent deadline, expired, rejected)
}
