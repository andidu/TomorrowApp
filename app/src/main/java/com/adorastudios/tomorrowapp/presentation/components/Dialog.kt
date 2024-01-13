@file:OptIn(ExperimentalAnimationApi::class)

package com.adorastudios.tomorrowapp.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp

@Composable
fun Dialog(
    open: Boolean,
    onClose: () -> Unit,
    innerPadding: PaddingValues = PaddingValues(
        top = 24.dp,
        bottom = 8.dp,
        start = 16.dp,
        end = 16.dp,
    ),
    content: @Composable ColumnScope.() -> Unit,
) {
    val dialogTransition = updateTransition(
        targetState = open,
        label = "dialog",
    )
    dialogTransition.AnimatedVisibility(
        visible = { it },
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        Scrim(
            modifier = Modifier.fillMaxSize(),
            onClose = {
                onClose()
            },
        )
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            dialogTransition.AnimatedVisibility(
                visible = { it },
                enter = slideInVertically { it / 4 },
                exit = slideOutVertically { it / 4 },
            ) {
                Column(
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.large)
                        .background(MaterialTheme.colorScheme.background)
                        .pointerInput(true) { detectTapGestures { } }
                        .padding(innerPadding),
                ) {
                    content()
                }
            }
        }
    }
}
