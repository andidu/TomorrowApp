package com.adorastudios.tomorrowapp.presentation.screens.todoList.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun DefaultImageRadioButton(
    modifier: Modifier = Modifier,
    imageVector: ImageVector,
    iconColor: Color = MaterialTheme.colorScheme.onBackground,
    contentDescription: String,
    selected: Boolean,
    onSelect: () -> Unit,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        RadioButton(
            selected = selected,
            onClick = onSelect,
        )
        Icon(
            imageVector = imageVector,
            tint = iconColor,
            contentDescription = contentDescription,
        )
    }
}
