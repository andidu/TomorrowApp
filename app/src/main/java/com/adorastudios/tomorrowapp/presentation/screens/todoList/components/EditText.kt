package com.adorastudios.tomorrowapp.presentation.screens.todoList.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp

@Composable
fun EditText(
    modifier: Modifier = Modifier,
    value: String,
    textStyle: TextStyle = TextStyle(),
    singleLine: Boolean = false,
    onValueChange: (String) -> Unit,
    placeholderValue: String = "",
    hintColor: Color,
    textColor: Color,
    internalPadding: PaddingValues = PaddingValues(0.dp),
) {
    Box(
        modifier = modifier,
    ) {
        BasicTextField(
            modifier = modifier
                .fillMaxWidth()
                .align(Alignment.CenterStart),
            value = value,
            onValueChange = onValueChange,
            textStyle = textStyle.copy(color = textColor),
            singleLine = singleLine,
            cursorBrush = SolidColor(MaterialTheme.colorScheme.onBackground),
        ) {
            Box(modifier = Modifier.padding(internalPadding)) { it() }
        }
        if (value.isEmpty()) {
            Text(
                modifier = modifier
                    .align(Alignment.CenterStart)
                    .padding(internalPadding),
                text = placeholderValue,
                style = textStyle,
                color = hintColor,
            )
        }
    }
}
