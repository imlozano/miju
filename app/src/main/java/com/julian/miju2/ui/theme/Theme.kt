package com.julian.miju2.ui.theme

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

// Esquema CLARO con la paleta real de MiJu.
private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = Color.White,
    secondary = Secondary,
    onSecondary = Color.White,
    background = Background,
    onBackground = OnSurface,
    surface = Background,
    onSurface = OnSurface,
    surfaceVariant = Neutral,
    onSurfaceVariant = OnSurfaceVariant,
    error = Error,
    onError = Color.White,
    outline = OnSurfaceVariant
)

// Esquema OSCURO coherente: índigo/teal aclarados sobre superficies oscuras.
private val DarkColorScheme = darkColorScheme(
    primary = PrimaryDarkTheme,
    onPrimary = Color(0xFF1A1A2E),
    secondary = SecondaryDarkTheme,
    onSecondary = Color(0xFF003733),
    background = DarkBackground,
    onBackground = DarkOnSurface,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    error = ErrorLight,
    onError = Color(0xFF410002),
    outline = DarkOnSurfaceVariant
)

@Composable
fun Miju2Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Color dinámico desactivado: MiJu usa siempre su propia paleta de marca.
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
