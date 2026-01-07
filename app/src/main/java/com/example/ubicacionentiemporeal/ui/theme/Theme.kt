package com.example.ubicacionentiemporeal.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Enumeración para seleccionar el tema
enum class AppTheme {
    IPN, ESCOM
}

// Esquemas de colores para IPN
private val IPNDarkColorScheme = darkColorScheme(
    primary = IPNGuinda,
    secondary = IPNGuindaContainer,
    tertiary = White
)

private val IPNLightColorScheme = lightColorScheme(
    primary = IPNGuinda,
    secondary = IPNGuindaDark,
    tertiary = Black
)

// Esquemas de colores para ESCOM
private val ESCOMDarkColorScheme = darkColorScheme(
    primary = ESCOMBlue,
    secondary = ESCOMBlueContainer,
    tertiary = White
)

private val ESCOMLightColorScheme = lightColorScheme(
    primary = ESCOMBlue,
    secondary = ESCOMBlueDark,
    tertiary = Black
)

@Composable
fun RastreadorESCOMTheme(
    appTheme: AppTheme = AppTheme.IPN, // Por defecto IPN
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Selección del esquema de color
    val colorScheme = when (appTheme) {
        AppTheme.IPN -> if (darkTheme) IPNDarkColorScheme else IPNLightColorScheme
        AppTheme.ESCOM -> if (darkTheme) ESCOMDarkColorScheme else ESCOMLightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Asegúrate de que Typography exista o quita esta línea si usas default
        content = content
    )
}