package com.adorastudios.tomorrowapp.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import com.adorastudios.tomorrowapp.R

@Composable
fun Scrim(modifier: Modifier = Modifier, onClose: () -> Unit) {
    val strClose = stringResource(id = R.string.contentDescription_close)
    Box(
        modifier
            .fillMaxSize()
            .pointerInput(onClose) { detectTapGestures { onClose() } }
            .semantics {
                onClick(strClose) { onClose(); true }
            }
            .focusable()
            .onKeyEvent {
                if (it.key == Key.Escape) {
                    onClose(); true
                } else {
                    false
                }
            }
            .background(Color.DarkGray.copy(alpha = 0.75f)),
    )
}
