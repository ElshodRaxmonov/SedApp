package com.example.sedapp.core.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = SedAppOrange,
    onPrimary = White,
    secondary = DeepOrange,
    onSecondary = White,
    tertiary = SoftGold,
    onTertiary = Charcoal,
    background = Charcoal,
    onBackground = White,
    surface = White,
    onSurface = Charcoal,
    surfaceVariant = TextFieldBackground,
    onSurfaceVariant = Charcoal,
    error = ErrorRed,
    onError = White,
    outline = Charcoal.copy(alpha = 0.12f)
)

@Composable
fun SedAppTheme(
    darkTheme: Boolean = false, 
    dynamicColor: Boolean = false, 
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
