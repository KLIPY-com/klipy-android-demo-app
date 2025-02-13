package com.klipy.demoapp.presentation.theme

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
    primary = VividYellow,
    secondary = VividYellow,
    tertiary = VividYellow,
    surface = Color(0xFF32343B),
    surfaceVariant = Color(0xFF36383F),
    background = Color(0xFF19191C),
    onPrimary = Color.Black
)

private val LightColorScheme = lightColorScheme(
    primary = VividYellow,
    secondary = VividYellow,
    tertiary = VividYellow,
    surface = Color(0xFFF4F4F9),
    surfaceVariant = Color(0xFFF8F8F9),
    background = Color(0xFFFEFEFE),
    onPrimary = Color.White
)

@Composable
fun KlipyDemoAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
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