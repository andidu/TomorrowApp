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
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccessTime
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowDropUp
import androidx.compose.material.icons.rounded.ArrowLeft
import androidx.compose.material.icons.rounded.ArrowRight
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.DeleteForever
import androidx.compose.material.icons.rounded.DoneAll
import androidx.compose.material.icons.rounded.RemoveDone
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import androidx.compose.ui.unit.toIntRect
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
        val editMode by remember(state.selectedTodos) {
            derivedStateOf {
                state.selectedTodos.isNotEmpty()
            }
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primaryContainer),
        ) {
            TopTile(
                state.topTileOpen,
                state.preferences,
                onNotificationClick = {
                    navController.navigate(Screens.Notifications.route)
                },
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

            AnimatedContent(
                targetState = editMode,
                label = "editMode",
                transitionSpec = { expandVertically() togetherWith shrinkVertically() },
            ) { targetState ->
                when (targetState) {
                    true -> {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                        ) {
                            IconButton(onClick = {
                                viewModel.onEvent(TodoListEvent.SelectionEvent.Delete)
                            }) {
                                Icon(
                                    imageVector = Icons.Rounded.DeleteForever,
                                    contentDescription = stringResource(id = R.string.contentDescription_deleteTodos),
                                )
                            }
                            IconButton(onClick = {
                                viewModel.onEvent(TodoListEvent.SelectionEvent.Check)
                            }) {
                                Icon(
                                    imageVector = Icons.Rounded.DoneAll,
                                    contentDescription = stringResource(id = R.string.contentDescription_doneTodos),
                                )
                            }
                            IconButton(onClick = {
                                viewModel.onEvent(TodoListEvent.SelectionEvent.Uncheck)
                            }) {
                                Icon(
                                    imageVector = Icons.Rounded.RemoveDone,
                                    contentDescription = stringResource(id = R.string.contentDescription_notDoneTodos),
                                )
                            }
                            IconButton(onClick = {
                                viewModel.onEvent(TodoListEvent.SelectionEvent.Deselect)
                            }) {
                                Icon(
                                    imageVector = Icons.Rounded.Close,
                                    contentDescription = stringResource(id = R.string.contentDescription_cancelSelection),
                                )
                            }
                        }
                    }

                    false -> {
                        TimeTile(
                            modifier = Modifier.fillMaxWidth(),
                            timePeriod = state.timePeriod,
                            timeTileOpen = state.timeTileOpen,
                            onChangeTimePeriod = {
                                viewModel.onEvent(TodoListEvent.TimeTileEvent.ChangeTimePeriod(it))
                            },
                            onOpenTimeTile = {
                                viewModel.onEvent(TodoListEvent.TimeTileEvent.OpenTimeTile(it))
                            },
                        )
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
                        onDone = { item ->
                            viewModel.onEvent(
                                TodoListEvent.TodoEvent.Done(item),
                            )
                        },
                        onSelect = { id, forceSelect ->
                            viewModel.onEvent(
                                TodoListEvent.SelectionEvent.Selected(
                                    id,
                                    forceSelect,
                                ),
                            )
                        },
                        onSelectRange = { initial, previous, current ->
                            viewModel.onEvent(
                                TodoListEvent.SelectionEvent.SelectedRange(
                                    initial,
                                    previous,
                                    current,
                                ),
                            )
                        },
                        selectionMode = editMode,
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
                        onDone = { item ->
                            viewModel.onEvent(
                                TodoListEvent.TodoEvent.Done(item),
                            )
                        },
                        onSelect = { id, forceSelect ->
                            viewModel.onEvent(
                                TodoListEvent.SelectionEvent.Selected(
                                    id,
                                    forceSelect,
                                ),
                            )
                        },
                        onSelectRange = { initial, previous, current ->
                            viewModel.onEvent(
                                TodoListEvent.SelectionEvent.SelectedRange(
                                    initial,
                                    previous,
                                    current,
                                ),
                            )
                        },
                        selectionMode = editMode,
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
                        onDone = { item ->
                            viewModel.onEvent(
                                TodoListEvent.TodoEvent.Done(item),
                            )
                        },
                        onSelect = { id, forceSelect ->
                            viewModel.onEvent(
                                TodoListEvent.SelectionEvent.Selected(
                                    id,
                                    forceSelect,
                                ),
                            )
                        },
                        onSelectRange = { initial, previous, current ->
                            viewModel.onEvent(
                                TodoListEvent.SelectionEvent.SelectedRange(
                                    initial,
                                    previous,
                                    current,
                                ),
                            )
                        },
                        selectionMode = editMode,
                        timePeriod = TimePeriod.Future,
                    )
                }
            }
        }
        AnimatedVisibility(
            modifier = Modifier.fillMaxSize(),
            visible = !editMode,
            enter = fadeIn() + slideInVertically { it / 2 },
            exit = fadeOut() + slideOutVertically { it / 2 },
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                FloatingActionButton(
                    modifier = Modifier
                        .wrapContentSize()
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
    }
}

@Composable
fun TimeTile(
    modifier: Modifier = Modifier,
    timePeriod: TimePeriod,
    timeTileOpen: Boolean,
    onChangeTimePeriod: (TimePeriod) -> Unit,
    onOpenTimeTile: (Boolean) -> Unit,
) {
    val showTimeOptions = updateTransition(
        targetState = timeTileOpen,
        label = "showTimeOptions",
    )
    val doteColor by showTimeOptions.animateColor(
        label = "doteColor",
    ) { open ->
        if (open) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer
    }

    Row(
        modifier = modifier,
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
                    it || timePeriod == TimePeriod.Past
                },
                enter = fadeIn() + expandVertically(),
                exit = shrinkVertically() + fadeOut(),
            ) {
                TextButton(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    onClick = {
                        onChangeTimePeriod(TimePeriod.Past)
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        disabledContentColor = MaterialTheme.colorScheme.primary,
                    ),
                    enabled = timePeriod != TimePeriod.Past,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.ArrowRight,
                        contentDescription = stringResource(id = if (timePeriod == TimePeriod.Past) R.string.contentDescription_selected else R.string.contentDescription_notSelected),
                        tint = if (timePeriod == TimePeriod.Past) doteColor else Color.Transparent,
                    )
                    Text(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        text = "Past",
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Icon(
                        imageVector = Icons.Rounded.ArrowLeft,
                        contentDescription = stringResource(id = if (timePeriod == TimePeriod.Past) R.string.contentDescription_selected else R.string.contentDescription_notSelected),
                        tint = if (timePeriod == TimePeriod.Past) doteColor else Color.Transparent,
                    )
                }
            }
            showTimeOptions.AnimatedVisibility(
                modifier = Modifier.fillMaxWidth(),
                visible = {
                    it || timePeriod == TimePeriod.Today
                },
                enter = fadeIn() + expandVertically(),
                exit = shrinkVertically() + fadeOut(),
            ) {
                TextButton(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    onClick = {
                        onChangeTimePeriod(TimePeriod.Today)
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        disabledContentColor = MaterialTheme.colorScheme.primary,
                    ),
                    enabled = timePeriod != TimePeriod.Today,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.ArrowRight,
                        contentDescription = stringResource(id = if (timePeriod == TimePeriod.Today) R.string.contentDescription_selected else R.string.contentDescription_notSelected),
                        tint = if (timePeriod == TimePeriod.Today) doteColor else Color.Transparent,
                    )
                    Text(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        text = "Today",
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Icon(
                        imageVector = Icons.Rounded.ArrowLeft,
                        contentDescription = stringResource(id = if (timePeriod == TimePeriod.Today) R.string.contentDescription_selected else R.string.contentDescription_notSelected),
                        tint = if (timePeriod == TimePeriod.Today) doteColor else Color.Transparent,
                    )
                }
            }
            showTimeOptions.AnimatedVisibility(
                modifier = Modifier.fillMaxWidth(),
                visible = {
                    it || timePeriod == TimePeriod.Future
                },
                enter = fadeIn() + expandVertically(),
                exit = shrinkVertically() + fadeOut(),
            ) {
                TextButton(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    onClick = {
                        onChangeTimePeriod(TimePeriod.Future)
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        disabledContentColor = MaterialTheme.colorScheme.primary,
                    ),
                    enabled = timePeriod != TimePeriod.Future,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.ArrowRight,
                        contentDescription = stringResource(id = if (timePeriod == TimePeriod.Future) R.string.contentDescription_selected else R.string.contentDescription_notSelected),
                        tint = if (timePeriod == TimePeriod.Future) doteColor else Color.Transparent,
                    )
                    Text(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        text = "Future",
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Icon(
                        imageVector = Icons.Rounded.ArrowLeft,
                        contentDescription = stringResource(id = if (timePeriod == TimePeriod.Future) R.string.contentDescription_selected else R.string.contentDescription_notSelected),
                        tint = if (timePeriod == TimePeriod.Future) doteColor else Color.Transparent,
                    )
                }
            }
        }
        IconButton(onClick = {
            onOpenTimeTile(!showTimeOptions.currentState)
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
}

@Composable
fun TodoList(
    modifier: Modifier,
    gridCells: GridCells,
    state: TodoListState,
    onNavigate: (Todo) -> Unit,
    onSelect: (Long, Boolean) -> Unit,
    onSelectRange: (Long, Long, Long) -> Unit,
    selectionMode: Boolean,
    onDone: (Todo) -> Unit,
    timePeriod: TimePeriod,
) {
    val lazyGridState = rememberLazyGridState()

    fun todoIdAtOffset(hitPoint: Offset): Long? =
        lazyGridState.layoutInfo.visibleItemsInfo.find { itemInfo ->
            itemInfo.size.toIntRect().contains(hitPoint.round() - itemInfo.offset)
        }?.key as? Long?

    val shouldShowDates by remember(timePeriod, state.todosToday) {
        derivedStateOf { timePeriod != TimePeriod.Today || state.todosToday.size > 1 }
    }
    LazyVerticalGrid(
        modifier = modifier.pointerInput(true) {
            var initialTodoId: Long? = null
            var currentTodoId: Long? = null
            detectDragGesturesAfterLongPress(
                onDragStart = { offset ->
                    todoIdAtOffset(offset)?.let {
                        initialTodoId = it
                        currentTodoId = it
                        onSelect(it, true)
                    }
                },
                onDragCancel = {
                    initialTodoId = null
                    currentTodoId = null
                },
                onDragEnd = {
                    initialTodoId = null
                    currentTodoId = null
                },
                onDrag = { change, _ ->
                    todoIdAtOffset(change.position)?.let {
                        if (it != currentTodoId) {
                            onSelectRange(
                                initialTodoId ?: return@let,
                                currentTodoId ?: return@let,
                                it,
                            )
                            currentTodoId = it
                        }
                    }
                },
            )
        },
        state = lazyGridState,
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
                val selected by remember(state.selectedTodos) { derivedStateOf { item.id in state.selectedTodos } }
                val action: () -> Unit = remember(item) {
                    { onDone(item) }
                }
                Box(
                    modifier = Modifier
                        .animateItemPlacement()
                        .clip(MaterialTheme.shapes.medium)
                        .then(
                            if (selectionMode) {
                                Modifier.clickable {
                                    onSelect(item.id ?: return@clickable, false)
                                }
                            } else {
                                Modifier.clickable { onNavigate(item) }
                            },
                        ),
                ) {
                    TodoItem(
                        todo = item,
                        isSelected = selected,
                        listViewType = state.preferences.listViewType,
                        onDone = action,
                    )
                }
            }
        }
    }
}
