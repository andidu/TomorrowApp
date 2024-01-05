package com.adorastudios.tomorrowapp.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf

internal val LocalPaddings = staticCompositionLocalOf { Paddings() }

object AppTheme {
    val paddings: Paddings
        @Composable
        @ReadOnlyComposable
        get() = LocalPaddings.current
}

@Composable
fun AppTheme(
    colorScheme: ColorScheme = MaterialTheme.colorScheme,
    shapes: Shapes = MaterialTheme.shapes,
    typography: Typography = MaterialTheme.typography,
    paddings: Paddings = Paddings(),
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalPaddings provides paddings) {
        MaterialTheme(
            colorScheme = colorScheme,
            shapes = shapes,
            typography = typography,
            content = content,
        )
    }
}
