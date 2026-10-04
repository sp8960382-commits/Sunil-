package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Love Doctor Romantic Gradient Color Palette
val NavyBackground = Color(0xFF0C0617)
val NavySurface = Color(0xFF140A26)
val NavyCard = Color(0xFF1C0F38)
val NavyCardElevated = Color(0xFF26144B)

val PinkPrimary = Color(0xFFFF2D87)
val PinkLight = Color(0xFFFF60A6)
val PinkGradientStart = Color(0xFFFF2D87)
val PinkGradientEnd = Color(0xFFFF6584)

val MagentaAccent = Color(0xFFD81B60)
val PurpleDeep = Color(0xFF4A148C)
val PurpleViolet = Color(0xFF7B1FA2)
val PurpleDark = Color(0xFF230D42)

val WhitePure = Color(0xFFFFFFFF)
val WhiteCard = Color(0xFFFFFFFF)
val LavenderBackground = Color(0xFFF8F5FC)
val LavenderCard = Color(0xFFF1EBF9)
val GrayBorder = Color(0xFFE5DDF0)

val TextDark = Color(0xFF181028)
val TextMuted = Color(0xFF756F88)
val TextLight = Color(0xFFF7F4FD)
val TextLightMuted = Color(0xFFB5ADC8)

val GreenSuccess = Color(0xFF00C853)
val GreenSuccessLight = Color(0xFFE8F5E9)
val OrangeWarning = Color(0xFFFF9800)
val OrangeWarningLight = Color(0xFFFFF3E0)
val BlueInfo = Color(0xFF2979FF)
val RedDanger = Color(0xFFFF1744)

// Premium Romantic Gradients
val LoveGradient = Brush.horizontalGradient(
    colors = listOf(PinkPrimary, Color(0xFFFF6584), PurpleViolet)
)

val RomanticGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFFFF2D87), Color(0xFF8E24AA))
)

val DarkRomanticGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFF230D42), Color(0xFF0C0617))
)

val HeaderGradient = Brush.horizontalGradient(
    colors = listOf(Color(0xFF8E24AA), Color(0xFFFF2D87))
)

val GoldenRewardGradient = Brush.horizontalGradient(
    colors = listOf(Color(0xFFFFB300), Color(0xFFFF8F00))
)
