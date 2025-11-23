package com.sustech.sus_community.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val NightGreen = Color(0xFF0E1E16) // background/surface
private val DeepGreen = Color(0xFF06402B) // primary
private val TealGray = Color(0xFF49796B) // secondary
private val Sand = Color(0xFFBBB791) // on colors / accents

private val SusCommunityDarkColors = darkColorScheme(
    primary = DeepGreen,
    onPrimary = Sand,
    secondary = TealGray,
    onSecondary = Sand,
    background = NightGreen,
    onBackground = Sand,
    surface = NightGreen,
    onSurface = Sand,
    primaryContainer = DeepGreen.copy(alpha = 0.85f),
    onPrimaryContainer = Sand,
    secondaryContainer = TealGray.copy(alpha = 0.85f),
    onSecondaryContainer = Sand,
    surfaceVariant = NightGreen.copy(alpha = 0.8f),
    onSurfaceVariant = Sand.copy(alpha = 0.9f),
)
private val SusCommunityLightColors = lightColorScheme(
    primary = Color(0xFF06402B),
    onPrimary = Color.White,          // better contrast on dark primary
    secondary = Color(0xFF0E1E16),
    onSecondary = Color.Black,
    background = Color(0xFFE4F1EB),
    onBackground = Color(0xFF191C1A),
    surface = Color(0xFFE4F1EB),
    onSurface = Color(0xFF0F1210),
    primaryContainer = Color(0xFF06402B),
    onPrimaryContainer = Color(0xFF9ED4B7),
    secondaryContainer = Color(0xFF06402B),
    onSecondaryContainer = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFDCE5DD),
    onSurfaceVariant = Color(0xFF303833),
)

@Composable
fun SusCommunityTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) SusCommunityDarkColors else SusCommunityLightColors
    MaterialTheme(colorScheme = colors, content = content)
}
