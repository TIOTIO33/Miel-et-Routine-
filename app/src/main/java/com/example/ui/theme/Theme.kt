package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val LightColorScheme = lightColorScheme(
    primary = HoneyPrimary,
    onPrimary = Color.White,
    primaryContainer = HoneyLight,
    onPrimaryContainer = HoneyPrimaryVariant,
    secondary = HoneySecondary,
    onSecondary = Color.White,
    secondaryContainer = SageGreenLight,
    onSecondaryContainer = SageGreenDark,
    tertiary = SkyBlue,
    onTertiary = Color.White,
    tertiaryContainer = SkyBlueLight,
    onTertiaryContainer = SkyBlueDark,
    background = HoneyBackground,
    onBackground = HoneyOnBackground,
    surface = HoneySurface,
    onSurface = HoneyOnSurface,
    surfaceVariant = HoneySurfaceVariant,
    onSurfaceVariant = Color(0xFF57534E),
    outline = HoneyOutline,
    outlineVariant = Color(0xFFF3EAD9)
)

private val DarkColorScheme = darkColorScheme(
    primary = HoneyDarkPrimary,
    onPrimary = Color(0xFF451A03),
    primaryContainer = Color(0xFF78350F),
    onPrimaryContainer = Color(0xFFFEF3C7),
    secondary = Color(0xFF34D399),
    onSecondary = Color(0xFF064E3B),
    secondaryContainer = Color(0xFF065F46),
    onSecondaryContainer = Color(0xFFA7F3D0),
    tertiary = Color(0xFF38BDF8),
    onTertiary = Color(0xFF082F49),
    background = HoneyDarkBackground,
    onBackground = Color(0xFFF5F5F4),
    surface = HoneyDarkSurface,
    onSurface = Color(0xFFF5F5F4),
    surfaceVariant = Color(0xFF3E3935),
    onSurfaceVariant = Color(0xFFD6D3D1)
)

val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = AppShapes,
        content = content
    )
}
