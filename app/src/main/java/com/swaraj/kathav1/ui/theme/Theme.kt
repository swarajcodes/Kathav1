package com.swaraj.kathav1.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Define your color scheme
val AppColorScheme = lightColorScheme(
    primary = Color(0xFFA3D749), // Primary green
    secondary = Color(0xFF4CAF50), // Secondary green
    background = Color.Black, // Dark background
    surface = Color.DarkGray, // Surface color
    onPrimary = Color.White, // Text on primary
    onSecondary = Color.White, // Text on secondary
    onBackground = Color.White, // Text on background
    onSurface = Color.White // Text on surface
)

// Use the theme in your app
@Composable
fun Kathav1Theme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AppColorScheme,
        content = content
    )
}