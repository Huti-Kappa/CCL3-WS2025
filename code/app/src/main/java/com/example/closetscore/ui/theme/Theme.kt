package com.example.closetscore.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = Red,
    secondary = Green,
    tertiary = Orange,
    background = Black,
    surface = DarkGrey,
    onBackground = White,
    onSurface = White,
    onPrimary = White,
)

private val LightColorScheme = lightColorScheme(
    primary = Red,
    secondary = Green,
    tertiary = Orange,
    background = White,
    surface = Grey,
    onBackground = Black,
    onSurface = Black,
    onPrimary = White,
)

@Composable
fun ClosetScoreTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}