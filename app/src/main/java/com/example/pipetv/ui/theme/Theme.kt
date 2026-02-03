package com.example.pipetv.ui.theme

import androidx.compose.runtime.Composable
import androidx.tv.material3.*
import androidx.compose.ui.graphics.Color

private val DarkColors = darkColorScheme(
    primary = Color(0xFFE50914), // Red highlight
    background = Color(0xFF0F0F0F), // True dark background
    surface = Color(0xFF1F1F1F),
    onSurface = Color.White
)

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun PipeTVTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColors,
        content = content
    )
}
