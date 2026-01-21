package com.example.closetscore.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    // In Dark Mode, we use the lighter green so it pops against the dark background
    primary = LightGreen,
    onPrimary = Green, // Text on the primary button looks best in the dark green
    primaryContainer = Green,
    onPrimaryContainer = Grey,

    // Berry Red for secondary actions
    secondary = BrandRed,
    onSecondary = White,

    // Orange for small accents (like Floating Action Buttons or Toggles)
    tertiary = BrandOrange,

    // Backgrounds using your Greys
    background = Black,
    surface = DarkestGrey, // Cards/Sheets use your DarkestGrey
    onBackground = Grey,   // Text is your Light Grey
    onSurface = White,

    // Standard error
    error = BrandRed
)

private val LightColorScheme = lightColorScheme(
    primary = LightGreen,
    onPrimary = White,
    primaryContainer = Grey,
    onPrimaryContainer = Green,

    // Berry Red adds a nice "Fashion" contrast to the Green
    secondary = BrandRed,
    onSecondary = White,

    // Orange for standout elements
    tertiary = BrandOrange,
    onTertiary = White,

    // Backgrounds
    background = White,
    surface = White,       // Cards are white
    surfaceVariant = Grey, // Text fields or dividers use your Light Grey

    // Text Colors
    onBackground = Black,
    onSurface = DarkestGrey, // Main text is your DarkestGrey (easier on eyes than Black)
    onSurfaceVariant = DarkGrey, // Subtitles are your Medium Grey
)

@Composable
fun ClosetScoreTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}