@file:OptIn(ExperimentalMaterial3Api::class)

package com.adorastudios.tomorrowapp.presentation.screens.addEditTodo

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccessTime
import androidx.compose.material.icons.rounded.Circle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.DeleteForever
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.adorastudios.tomorrowapp.R
import com.adorastudios.tomorrowapp.domain.MILLIS_TO_DAYS
import com.adorastudios.tomorrowapp.domain.currentDay
import com.adorastudios.tomorrowapp.domain.getDate
import com.adorastudios.tomorrowapp.domain.model.TodoColor
import com.adorastudios.tomorrowapp.domain.model.TodoType
import com.adorastudios.tomorrowapp.presentation.components.Dialog
import com.adorastudios.tomorrowapp.presentation.screens.todoList.components.EditText
import com.adorastudios.tomorrowapp.ui.theme.bottomShape
import com.adorastudios.tomorrowapp.ui.theme.topShape
import kotlinx.coroutines.flow.collectLatest

@Composable
fun AddEditTodoScreen(
    navController: NavController,
    viewModel: AddEditTodoViewModel = hiltViewModel(),
) {
    val dark = isSystemInDarkTheme()
    val titleColor by remember { derivedStateOf { viewModel.color.value.get(dark) } }
    var openDateDialog by remember { mutableStateOf(false) }
    val datePickerState = viewModel.date
    val context = LocalContext.current
    var toast by remember {
        mutableStateOf<Toast?>(null)
    }

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                AddEditTodoUiEvent.Back -> {
                    navController.popBackStack()
                }

                AddEditTodoUiEvent.Error.EmptyTitle -> {
                    toast?.cancel()
                    toast = Toast.makeText(
                        context,
                        context.getString(R.string.addEditTodoScreen_emptyTitleError),
                        Toast.LENGTH_SHORT,
                    )

                    toast?.show()
                }
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
        ) {
            var editFieldVisible by remember {
                mutableStateOf(false)
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 6.dp,
                        shape = topShape,
                    )
                    .clip(topShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .statusBarsPadding(),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clip(CircleShape)
                        .background(titleColor),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(
                        onClick = { editFieldVisible = !editFieldVisible },
                    ) {
                        when (val state = viewModel.type.value) {
                            TodoType.Undefined -> {
                                Icon(
                                    imageVector = Icons.Rounded.Edit,
                                    contentDescription = stringResource(id = R.string.contentDescription_editTypeAndColor),
                                )
                            }

                            is TodoType.Defined -> {
                                Icon(
                                    imageVector = state.icon,
                                    contentDescription = stringResource(id = state.descriptionId),
                                )
                            }
                        }
                    }
                    EditText(
                        modifier = Modifier.weight(1f),
                        value = viewModel.title.value,
                        onValueChange = {
                            viewModel.onEvent(AddEditTodoEvent.TitleChanged(it))
                        },
                        textStyle = MaterialTheme.typography.titleLarge,
                        placeholderValue = stringResource(id = R.string.addEditTodoScreen_titlePlaceholder),
                        hintColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(
                            alpha = 0.7f,
                        ),
                        textColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }

                AnimatedVisibility(visible = editFieldVisible) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                    ) {
                        Text(
                            modifier = Modifier.padding(start = 16.dp),
                            text = stringResource(id = R.string.addEditTodoScreen_chooseColor),
                            style = MaterialTheme.typography.labelMedium,
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                        ) {
                            val colors = remember {
                                listOf(
                                    TodoColor.Red,
                                    TodoColor.Yellow,
                                    TodoColor.Green,
                                    TodoColor.Blue,
                                    TodoColor.Purple,
                                )
                            }
                            colors.forEachIndexed { index, color ->
                                IconButton(
                                    onClick = {
                                        viewModel.onEvent(AddEditTodoEvent.ColorChanged(color))
                                    },
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Circle,
                                        contentDescription = stringResource(
                                            id = R.string.contentDescription_color,
                                            index,
                                        ),
                                        tint = color.get(dark),
                                    )
                                }
                            }
                            IconButton(
                                onClick = {
                                    viewModel.onEvent(AddEditTodoEvent.ColorChanged(TodoColor.Transparent))
                                },
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Close,
                                    contentDescription = stringResource(id = R.string.contentDescription_colorNone),
                                    tint = MaterialTheme.colorScheme.onBackground,
                                )
                            }
                        }

                        Text(
                            modifier = Modifier.padding(start = 16.dp),
                            text = stringResource(id = R.string.addEditTodoScreen_chooseType),
                            style = MaterialTheme.typography.labelMedium,
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                        ) {
                            val types = remember {
                                listOf(
                                    TodoType.Defined.Work,
                                    TodoType.Defined.Study,
                                    TodoType.Defined.Travel,
                                    TodoType.Defined.Home,
                                    TodoType.Defined.Health,
                                )
                            }
                            types.forEach { type ->
                                IconButton(
                                    onClick = {
                                        viewModel.onEvent(AddEditTodoEvent.TypeChanged(type))
                                    },
                                ) {
                                    Icon(
                                        imageVector = type.icon,
                                        contentDescription = stringResource(id = type.descriptionId),
                                    )
                                }
                            }
                            IconButton(
                                onClick = {
                                    viewModel.onEvent(AddEditTodoEvent.TypeChanged(TodoType.Undefined))
                                },
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Close,
                                    contentDescription = stringResource(id = R.string.contentDescription_typeNone),
                                    tint = MaterialTheme.colorScheme.onBackground,
                                )
                            }
                        }
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val dateText by remember {
                    derivedStateOf {
                        val date = viewModel.date.selectedDateMillis
                            ?.div(MILLIS_TO_DAYS)
                            ?.toInt() ?: currentDay()
                        date.getDate()
                    }
                }
                Box(modifier = Modifier.size(48.dp))
                Text(
                    modifier = Modifier.weight(1f),
                    text = dateText,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                )
                IconButton(onClick = {
                    openDateDialog = true
                }) {
                    Icon(
                        imageVector = Icons.Rounded.AccessTime,
                        contentDescription = stringResource(id = R.string.contentDescription_changeDate),
                    )
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .shadow(
                        elevation = 6.dp,
                        shape = bottomShape,
                    )
                    .clip(bottomShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
            ) {
                EditText(
                    modifier = Modifier.fillMaxSize(),
                    value = viewModel.content.value,
                    onValueChange = {
                        viewModel.onEvent(AddEditTodoEvent.ContentChanged(it))
                    },
                    textStyle = MaterialTheme.typography.bodyMedium,
                    placeholderValue = stringResource(id = R.string.addEditTodoScreen_contentPlaceholder),
                    hintColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(
                        alpha = 0.7f,
                    ),
                    textColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    internalPadding = PaddingValues(12.dp),
                )
            }
        }
        FloatingActionButton(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 16.dp, bottom = 16.dp),
            onClick = {
                viewModel.onEvent(AddEditTodoEvent.DeleteTodo)
            },
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.error,
        ) {
            Icon(
                imageVector = Icons.Rounded.DeleteForever,
                contentDescription = stringResource(R.string.contentDescription_deleteTodo),
            )
        }
        FloatingActionButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 16.dp),
            onClick = {
                viewModel.onEvent(AddEditTodoEvent.SaveTodo)
            },
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.primary,
        ) {
            Icon(
                imageVector = Icons.Rounded.Save,
                contentDescription = stringResource(R.string.contentDescription_saveTodo),
            )
        }
        Dialog(
            open = openDateDialog,
            innerPadding = PaddingValues(top = 8.dp, bottom = 8.dp, start = 0.dp, end = 0.dp),
            onClose = { openDateDialog = false },
        ) {
            DatePicker(state = datePickerState)
            Row(
                modifier = Modifier.align(Alignment.End).padding(0.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                TextButton(
                    onClick = {
                        openDateDialog = false
                    },
                ) {
                    Text(stringResource(id = android.R.string.ok))
                }
                TextButton(
                    onClick = {
                        openDateDialog = false
                    },
                ) {
                    Text(stringResource(id = android.R.string.cancel))
                }
            }
        }
    }
}
