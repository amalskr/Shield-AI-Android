package com.ceylonapz.shield_ai

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/** Same tokens as the web app so both surfaces read as one product. */
object ShieldColors {
    val SafeLight = Color(0xFF12805A)
    val WarnLight = Color(0xFFA86300)
    val ScamLight = Color(0xFFC42B33)
    val SafeDark = Color(0xFF3CC98F)
    val WarnDark = Color(0xFFF0AE45)
    val ScamDark = Color(0xFFFF6B72)
    val BubbleLight = Color(0xFFE9EFFB)
    val BubbleDark = Color(0xFF1A2853)
    val MarkLight = Color(0xFFFCE1E3)
    val MarkDark = Color(0x3DFF6B72)
}

private val Light = lightColorScheme(
    primary = Color(0xFF1F5BE0),
    onPrimary = Color.White,
    secondary = Color(0xFF0E2350),
    onSecondary = Color(0xFFF4F7FD),
    background = Color(0xFFF4F7FD),
    onBackground = Color(0xFF0E2350),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF0E2350),
    onSurfaceVariant = Color(0xFF56648A),
    outline = Color(0xFFD6DFEF),
    error = ShieldColors.ScamLight,
)

private val Dark = darkColorScheme(
    primary = Color(0xFF5B8CFF),
    onPrimary = Color(0xFF06102B),
    secondary = Color(0xFFE7EDFB),
    onSecondary = Color(0xFF0A1330),
    background = Color(0xFF0A1330),
    onBackground = Color(0xFFE7EDFB),
    surface = Color(0xFF111C3E),
    onSurface = Color(0xFFE7EDFB),
    onSurfaceVariant = Color(0xFF9DABCD),
    outline = Color(0xFF253463),
    error = ShieldColors.ScamDark,
)

@Composable
fun ShieldTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = if (isSystemInDarkTheme()) Dark else Light, content = content)
}
