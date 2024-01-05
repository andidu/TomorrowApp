package com.adorastudios.tomorrowapp.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme()

private val LightColorScheme = lightColorScheme()

val bottomShape = RoundedCornerShape(
    topStart = 16.0.dp,
    topEnd = 16.0.dp,
    bottomEnd = 0.0.dp,
    bottomStart = 0.0.dp,
)

val topShape = RoundedCornerShape(
    topStart = 0.0.dp,
    topEnd = 0.0.dp,
    bottomEnd = 16.0.dp,
    bottomStart = 16.0.dp,
)

@Composable
fun TomorrowAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    AppTheme(
        colorScheme = colorScheme,
        paddings = Paddings(),
        content = content,
    )
}
