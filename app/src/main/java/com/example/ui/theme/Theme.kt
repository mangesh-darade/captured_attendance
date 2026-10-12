package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val KioskColorScheme = lightColorScheme(
    primary = BrandPrimary,
    onPrimary = Color.White,
    primaryContainer = BrandActiveTint,
    onPrimaryContainer = BrandPrimary,
    background = PageBackground,
    surface = PanelBackground,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    outline = BorderDefault
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = KioskColorScheme,
        typography = Typography,
        content = content
    )
}

