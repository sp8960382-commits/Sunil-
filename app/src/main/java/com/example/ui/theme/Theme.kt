package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = PinkPrimary,
    onPrimary = Color.White,
    primaryContainer = PurpleDark,
    onPrimaryContainer = PinkLight,
    secondary = PurpleViolet,
    onSecondary = Color.White,
    secondaryContainer = NavyCardElevated,
    onSecondaryContainer = TextLight,
    tertiary = MagentaAccent,
    background = NavyBackground,
    onBackground = TextLight,
    surface = NavySurface,
    onSurface = TextLight,
    surfaceVariant = NavyCard,
    onSurfaceVariant = TextLightMuted,
    outline = PurpleViolet.copy(alpha = 0.4f),
    error = RedDanger
)

private val LightColorScheme = lightColorScheme(
    primary = PinkPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFD8E6),
    onPrimaryContainer = Color(0xFF3E001F),
    secondary = PurpleViolet,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFF3E5F5),
    onSecondaryContainer = PurpleDark,
    tertiary = MagentaAccent,
    background = LavenderBackground,
    onBackground = TextDark,
    surface = WhiteCard,
    onSurface = TextDark,
    surfaceVariant = LavenderCard,
    onSurfaceVariant = TextMuted,
    outline = GrayBorder,
    error = RedDanger
)

@Composable
fun LoveDoctorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // We intentionally use our custom Romantic Light/Dark theme rather than dynamic Android colors
    // to strictly preserve the Love Doctor visual brand identity (deep purple + pink + white cards)
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
