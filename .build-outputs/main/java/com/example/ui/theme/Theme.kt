package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = CyberGreen,
    secondary = CyberCyan,
    tertiary = PremiumPink,
    background = SpaceBlack,
    surface = SpaceDark,
    surfaceVariant = DarkCardBg,
    onPrimary = SpaceBlack,
    onSecondary = SpaceBlack,
    onBackground = DarkTextHeader,
    onSurface = DarkTextHeader,
    onSurfaceVariant = DarkTextBody
  )

private val LightColorScheme = DarkColorScheme // Always premium dark mode for matching the Apps2App dark experience

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Force dark mode to match website theme
  dynamicColor: Boolean = false, // Disable dynamic colors to preserve our cohesive cyberpunk design brand
  content: @Composable () -> Unit,
) {
  val colorScheme = DarkColorScheme

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
