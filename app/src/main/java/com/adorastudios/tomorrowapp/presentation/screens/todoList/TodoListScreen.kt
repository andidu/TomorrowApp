@file:OptIn(ExperimentalAnimationApi::class, ExperimentalFoundationApi::class)

package com.adorastudios.tomorrowapp.presentation.screens.todoList

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccessTime
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowDropUp
import androidx.compose.material.icons.rounded.ArrowLeft
import androidx.compose.material.icons.rounded.ArrowRight
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.adorastudios.tomorrowapp.R
import com.adorastudios.tomorrowapp.domain.getDate
import com.adorastudios.tomorrowapp.domain.model.Todo
import com.adorastudios.tomorrowapp.presentation.screens.Screens
import com.adorastudios.tomorrowapp.presentation.screens.todoList.components.TopTile
import com.adorastudios.tomorrowapp.presentation.screens.todoList.components.grid.VeryAdaptive
import com.adorastudios.tomorrowapp.presentation.screens.todoList.components.todo.TodoItem
import com.adorastudios.tomorrowapp.ui.theme.bottomShape

@Composable
fun TodoListScreen(
    navController: NavController,
    viewModel: TodoListViewModel = hiltViewModel(),
) {
    val state = viewModel.state.value

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primaryContainer),
        ) {
            TopTile(
                state.topTileOpen,
                state.preferences,
                onOpen = {
                    viewModel.onEvent(TodoListEvent.TopTileEvent.OpenTopTile(it))
                },
                onOptionSelected = {
                    viewModel.onEvent(TodoListEvent.TopTileEvent.ChangeListViewType(it))
                },
                onShowOverdueInTodaySelected = {
                    viewModel.onEvent(TodoListEvent.TopTileEvent.ChangeShowOverdueInToday(it))
                },
                onMoveDoneToPastSelected = {
                    viewModel.onEvent(TodoListEvent.TopTileEvent.ChangeMoveDoneToPast(it))
                },
            )
            val showTimeOptions = updateTransition(
                targetState = state.timeTileOpen,
                label = "showTimeOptions",
            )
            val doteColor by showTimeOptions.animateColor(
                label = "doteColor",
            ) { open ->
                if (open) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier.size(48.dp),
                )
                Column(
                    modifier = Modifier.weight(1f),
                ) {
                    showTimeOptions.AnimatedVisibility(
                        modifier = Modifier.fillMaxWidth(),
                        visible = {
                            it || state.timePeriod == TimePeriod.Past
                        },
                        enter = fadeIn() + expandVertically(),
                        exit = shrinkVertically() + fadeOut(),
                    ) {
                        TextButton(
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            onClick = {
                                viewModel.onEvent(
                                    TodoListEvent.TimeTileEvent.ChangeTimePeriod(TimePeriod.Past),
                                )
                            },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                disabledContentColor = MaterialTheme.colorScheme.primary,
                            ),
                            enabled = state.timePeriod != TimePeriod.Past,
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.ArrowRight,
                                contentDescription = stringResource(id = if (state.timePeriod == TimePeriod.Past) R.string.contentDescription_selected else R.string.contentDescription_notSelected),
                                tint = if (state.timePeriod == TimePeriod.Past) doteColor else Color.Transparent,
                            )
                            Text(
                                modifier = Modifier.padding(horizontal = 8.dp),
                                text = "Past",
                                style = MaterialTheme.typography.titleMedium,
                            )
                            Icon(
                                imageVector = Icons.Rounded.ArrowLeft,
                                contentDescription = stringResource(id = if (state.timePeriod == TimePeriod.Past) R.string.contentDescription_selected else R.string.contentDescription_notSelected),
                                tint = if (state.timePeriod == TimePeriod.Past) doteColor else Color.Transparent,
                            )
                        }
                    }
                    showTimeOptions.AnimatedVisibility(
                        modifier = Modifier.fillMaxWidth(),
                        visible = {
                            it || state.timePeriod == TimePeriod.Today
                        },
                        enter = fadeIn() + expandVertically(),
                        exit = shrinkVertically() + fadeOut(),
                    ) {
                        TextButton(
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            onClick = {
                                viewModel.onEvent(
                                    TodoListEvent.TimeTileEvent.ChangeTimePeriod(TimePeriod.Today),
                                )
                            },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                disabledContentColor = MaterialTheme.colorScheme.primary,
                            ),
                            enabled = state.timePeriod != TimePeriod.Today,
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.ArrowRight,
                                contentDescription = stringResource(id = if (state.timePeriod == TimePeriod.Today) R.string.contentDescription_selected else R.string.contentDescription_notSelected),
                                tint = if (state.timePeriod == TimePeriod.Today) doteColor else Color.Transparent,
                            )
                            Text(
                                modifier = Modifier.padding(horizontal = 8.dp),
                                text = "Today",
                                style = MaterialTheme.typography.titleMedium,
                            )
                            Icon(
                                imageVector = Icons.Rounded.ArrowLeft,
                                contentDescription = stringResource(id = if (state.timePeriod == TimePeriod.Today) R.string.contentDescription_selected else R.string.contentDescription_notSelected),
                                tint = if (state.timePeriod == TimePeriod.Today) doteColor else Color.Transparent,
                            )
                        }
                    }
                    showTimeOptions.AnimatedVisibility(
                        modifier = Modifier.fillMaxWidth(),
                        visible = {
                            it || state.timePeriod == TimePeriod.Future
                        },
                        enter = fadeIn() + expandVertically(),
                        exit = shrinkVertically() + fadeOut(),
                    ) {
                        TextButton(
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            onClick = {
                                viewModel.onEvent(
                                    TodoListEvent.TimeTileEvent.ChangeTimePeriod(TimePeriod.Future),
                                )
                            },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                disabledContentColor = MaterialTheme.colorScheme.primary,
                            ),
                            enabled = state.timePeriod != TimePeriod.Future,
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.ArrowRight,
                                contentDescription = stringResource(id = if (state.timePeriod == TimePeriod.Future) R.string.contentDescription_selected else R.string.contentDescription_notSelected),
                                tint = if (state.timePeriod == TimePeriod.Future) doteColor else Color.Transparent,
                            )
                            Text(
                                modifier = Modifier.padding(horizontal = 8.dp),
                                text = "Future",
                                style = MaterialTheme.typography.titleMedium,
                            )
                            Icon(
                                imageVector = Icons.Rounded.ArrowLeft,
                                contentDescription = stringResource(id = if (state.timePeriod == TimePeriod.Future) R.string.contentDescription_selected else R.string.contentDescription_notSelected),
                                tint = if (state.timePeriod == TimePeriod.Future) doteColor else Color.Transparent,
                            )
                        }
                    }
                }
                IconButton(onClick = {
                    viewModel.onEvent(
                        TodoListEvent.TimeTileEvent.OpenTimeTile(
                            !showTimeOptions.currentState,
                        ),
                    )
                }) {
                    showTimeOptions.AnimatedContent {
                        when (it) {
                            true -> {
                                Icon(
                                    imageVector = Icons.Rounded.ArrowDropUp,
                                    contentDescription = stringResource(id = R.string.contentDescription_closeTime),
                                )
                            }

                            false -> {
                                Icon(
                                    imageVector = Icons.Rounded.AccessTime,
                                    contentDescription = stringResource(id = R.string.contentDescription_openTime),
                                )
                            }
                        }
                    }
                }
            }

            val gridCells = remember(state.preferences.listViewType) {
                when (state.preferences.listViewType) {
                    ListViewType.SmallGrid -> VeryAdaptive(150.dp, 3)
                    ListViewType.TitleAndContent -> GridCells.Adaptive(500.dp)
                    ListViewType.TitleOnly -> GridCells.Adaptive(500.dp)
                    ListViewType.LargeGrid -> VeryAdaptive(300.dp, 2)
                }
            }
            val changeLists = updateTransition(
                targetState = state.timePeriod,
                label = "timePeriod",
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .shadow(
                        elevation = 6.dp,
                        shape = bottomShape,
                    )
                    .clip(bottomShape)
                    .background(MaterialTheme.colorScheme.background),
            ) {
                changeLists.AnimatedVisibility(
                    visible = { it == TimePeriod.Past },
                    enter = slideInHorizontally() + fadeIn(),
                    exit = fadeOut(),
                ) {
                    TodoList(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                            .clip(MaterialTheme.shapes.medium),
                        gridCells = gridCells,
                        state = state,
                        onNavigate = { item ->
                            navController.navigate(Screens.AddEditTodo.route + "?todoId=${item.id}")
                        },
                        onDone = { item, done ->
                            viewModel.onEvent(
                                TodoListEvent.TodoEvent.Done(
                                    item,
                                    done,
                                ),
                            )
                        },
                        timePeriod = TimePeriod.Past,
                    )
                }
                changeLists.AnimatedVisibility(
                    visible = { it == TimePeriod.Today },
                    enter = if (changeLists.currentState == TimePeriod.Future) {
                        slideInHorizontally()
                    } else {
                        slideInHorizontally { it / 2 }
                    } + fadeIn(),
                    exit = fadeOut(),
                ) {
                    TodoList(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                            .clip(MaterialTheme.shapes.medium),
                        gridCells = gridCells,
                        state = state,
                        onNavigate = { item ->
                            navController.navigate(Screens.AddEditTodo.route + "?todoId=${item.id}")
                        },
                        onDone = { item, done ->
                            viewModel.onEvent(
                                TodoListEvent.TodoEvent.Done(
                                    item,
                                    done,
                                ),
                            )
                        },
                        timePeriod = TimePeriod.Today,
                    )
                }
                changeLists.AnimatedVisibility(
                    visible = { it == TimePeriod.Future },
                    enter = slideInHorizontally { it / 2 } + fadeIn(),
                    exit = fadeOut(),
                ) {
                    TodoList(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                            .clip(MaterialTheme.shapes.medium),
                        gridCells = gridCells,
                        state = state,
                        onNavigate = { item ->
                            navController.navigate(Screens.AddEditTodo.route + "?todoId=${item.id}")
                        },
                        onDone = { item, done ->
                            viewModel.onEvent(
                                TodoListEvent.TodoEvent.Done(
                                    item,
                                    done,
                                ),
                            )
                        },
                        timePeriod = TimePeriod.Future,
                    )
                }
            }
        }
        FloatingActionButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 16.dp),
            onClick = {
                navController.navigate(Screens.toAddEditScreen())
            },
        ) {
            Icon(
                imageVector = Icons.Rounded.Add,
                contentDescription = stringResource(R.string.contentDescription_addTodo),
            )
        }
    }
}

@Composable
fun TodoList(
    modifier: Modifier,
    gridCells: GridCells,
    state: TodoListState,
    onNavigate: (Todo) -> Unit,
    onDone: (Todo, Boolean) -> Unit,
    timePeriod: TimePeriod,
) {
    val shouldShowDates by remember {
        derivedStateOf { timePeriod != TimePeriod.Today || state.todosToday.size > 1 }
    }
    LazyVerticalGrid(
        modifier = modifier,
        columns = gridCells,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(bottom = 72.dp),

    ) {
        for (list in when (timePeriod) {
            TimePeriod.Future -> state.todosFuture
            TimePeriod.Past -> state.todosPast
            TimePeriod.Today -> state.todosToday
        }) {
            if (shouldShowDates) {
                val dateString = list.key.getDate()
                item(
                    key = dateString,
                    span = {
                        GridItemSpan(maxLineSpan)
                    },
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            modifier = Modifier.padding(8.dp),
                            text = dateString,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
            items(
                items = list.value,
                key = { item -> item.id ?: -1 },
            ) { item ->
                Box(modifier = Modifier.animateItemPlacement()) {
                    TodoItem(
                        modifier = Modifier
                            .combinedClickable(
                                onClick = { onNavigate(item) },
                                onLongClick = {
                                },
                            ),
                        todo = item,
                        isSelected = false,
                        listViewType = state.preferences.listViewType,
                        onDone = { done -> onDone(item, done) },
                    )
                }
            }
        }
    }
}
