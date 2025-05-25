package com.example.matrixcalculator.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

val MatrixGreen = Color(0xFF00FF41)
val MatrixGreenDarker = Color(0xFF008F11)
val MatrixGreenFaded = Color(0xFF00DD1F)
val MatrixBlack = Color(0xFF000000)
val MatrixDarkGray = Color(0xFF101010)
val MatrixCode = Color(0xFF29FF29)
val MatrixRed = Color(0xFFFF0000)

private val MatrixColorScheme = darkColorScheme(
    primary = MatrixGreen,
    onPrimary = MatrixBlack,
    primaryContainer = MatrixDarkGray,
    onPrimaryContainer = MatrixGreen,
    secondary = MatrixGreenDarker,
    onSecondary = MatrixBlack,
    secondaryContainer = MatrixBlack.copy(alpha = 0.8f),
    onSecondaryContainer = MatrixGreen,
    tertiary = MatrixGreenFaded,
    onTertiary = MatrixBlack,
    tertiaryContainer = MatrixBlack,
    onTertiaryContainer = MatrixGreenFaded,
    error = MatrixRed,
    errorContainer = MatrixBlack,
    onError = MatrixBlack,
    onErrorContainer = MatrixRed,
    background = MatrixBlack,
    onBackground = MatrixGreen,
    surface = MatrixDarkGray,
    onSurface = MatrixGreen,
    surfaceVariant = MatrixDarkGray.copy(alpha = 0.7f),
    onSurfaceVariant = MatrixGreenFaded,
    outline = MatrixGreen.copy(alpha = 0.6f),
    outlineVariant = MatrixGreenDarker
)

@Composable
fun MatrixCalculatorTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = MatrixColorScheme
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = MatrixBlack.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}