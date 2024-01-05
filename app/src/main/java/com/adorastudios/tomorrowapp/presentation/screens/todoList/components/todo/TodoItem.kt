package com.adorastudios.tomorrowapp.presentation.screens.todoList.components.todo

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircleOutline
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.adorastudios.tomorrowapp.R
import com.adorastudios.tomorrowapp.domain.model.Todo
import com.adorastudios.tomorrowapp.domain.model.TodoType
import com.adorastudios.tomorrowapp.presentation.screens.todoList.ListViewType

@Composable
fun TodoItem(
    modifier: Modifier = Modifier,
    todo: Todo,
    isSelected: Boolean,
    listViewType: ListViewType,
    onDone: (Boolean) -> Unit,
) {
    when (listViewType) {
        ListViewType.SmallGrid -> TodoItemWithContent(
            modifier = modifier,
            todo = todo,
            isSelected = isSelected,
            fixedRatio = true,
            onDone = onDone,
        )

        ListViewType.TitleOnly -> TodoWithNoContent(
            modifier = modifier,
            todo = todo,
            isSelected = isSelected,
            onDone = onDone,
        )

        ListViewType.LargeGrid -> TodoItemWithContent(
            modifier = modifier,
            todo = todo,
            isSelected = isSelected,
            fixedRatio = true,
            onDone = onDone,
        )

        ListViewType.TitleAndContent -> TodoItemWithContent(
            modifier = modifier,
            todo = todo,
            isSelected = isSelected,
            fixedRatio = false,
            onDone = onDone,
        )
    }
}

@Composable
fun TodoWithNoContent(
    modifier: Modifier = Modifier,
    todo: Todo,
    isSelected: Boolean,
    onDone: (Boolean) -> Unit,
) {
    val dark = isSystemInDarkTheme()

    BaseTodo(
        modifier = modifier,
        isSelected = isSelected,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 8.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
        ) {
            TodoItemTitle(
                title = todo.title,
                type = todo.type,
                color = todo.color.get(dark),
                done = todo.done,
                onDone = onDone,
            )
        }
    }
}

@Composable
fun TodoItemWithContent(
    modifier: Modifier = Modifier,
    todo: Todo,
    isSelected: Boolean,
    fixedRatio: Boolean,
    onDone: (Boolean) -> Unit,
) {
    val dark = isSystemInDarkTheme()
    val onSurface by animateColorAsState(
        if (isSelected) {
            MaterialTheme.colorScheme.onPrimaryContainer
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
        label = "contentColor",
    )

    BaseTodo(
        modifier = if (fixedRatio) modifier.aspectRatio(1f) else modifier,
        isSelected = isSelected,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 8.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
        ) {
            TodoItemTitle(
                modifier = Modifier.padding(start = 0.dp, end = 0.dp, bottom = 0.dp, top = 0.dp),
                title = todo.title,
                color = todo.color.get(dark),
                type = todo.type,
                done = todo.done,
                onDone = onDone,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = if (fixedRatio) Modifier.weight(1f) else Modifier,
            ) {
                Text(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp, end = 8.dp, top = 0.dp, bottom = 0.dp)
                        .align(Alignment.Top),
                    maxLines = if (fixedRatio) Int.MAX_VALUE else 10,
                    text = todo.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = onSurface,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
fun BaseTodo(
    modifier: Modifier = Modifier,
    isSelected: Boolean,
    content: @Composable (BoxScope.() -> Unit),
) {
    val surface by animateColorAsState(
        if (isSelected) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.surfaceVariant
        },
        label = "contentColor",
    )

    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .background(surface),
    ) {
        content()
    }
}

@Composable
fun TodoItemTitle(
    modifier: Modifier = Modifier,
    title: String,
    type: TodoType,
    color: Color,
    done: Boolean,
    onDone: (Boolean) -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = color,
                shape = CircleShape,
            )
            .padding(8.dp, 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        when (type) {
            TodoType.Undefined -> {
                Box(modifier = Modifier.size(30.dp))
            }

            is TodoType.Defined -> {
                Box(
                    modifier = Modifier.size(30.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = type.icon,
                        contentDescription = stringResource(id = type.descriptionId),
                    )
                }
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(
            modifier = Modifier.size(30.dp),
            onClick = {
                onDone(!done)
            },
        ) {
            Icon(
                imageVector = if (done) Icons.Rounded.CheckCircleOutline else Icons.Rounded.RadioButtonUnchecked,
                contentDescription = stringResource(id = if (done) R.string.contentDescription_done else R.string.contentDescription_notDone),
            )
        }
    }
}
