package com.adorastudios.tomorrowapp.presentation.screens.todoList

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adorastudios.tomorrowapp.domain.settings.SettingsRepository
import com.adorastudios.tomorrowapp.domain.useCases.GetTodos
import com.adorastudios.tomorrowapp.domain.useCases.TodoUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.min

@HiltViewModel
class TodoListViewModel @Inject constructor(
    private val todoUseCases: TodoUseCases,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {
    private val _state = mutableStateOf(TodoListState())
    val state: State<TodoListState> = _state

    private var loadTodayTodosJob: Job? = null
    private var loadPastTodosJob: Job? = null
    private var loadFutureTodosJob: Job? = null

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val preferences = settingsRepository.getPreferences()

            withContext(Dispatchers.Main) {
                _state.value = state.value.copy(preferences = preferences)
                loadAllTodos()
            }
        }
    }

    fun onEvent(event: TodoListEvent) {
        when (event) {
            is TodoListEvent.TopTileEvent -> {
                onEvent(event)
            }

            is TodoListEvent.TimeTileEvent -> {
                onEvent(event)
            }

            is TodoListEvent.TodoEvent.Done -> {
                viewModelScope.launch(Dispatchers.IO) {
                    todoUseCases.insertTodo(
                        todo = event.todo.copy(
                            done = !event.todo.done,
                        ),
                    )
                }
            }

            is TodoListEvent.SelectionEvent -> {
                onEvent(event)
            }
        }
    }

    private fun onEvent(event: TodoListEvent.TopTileEvent) {
        when (event) {
            is TodoListEvent.TopTileEvent.ChangeListViewType -> {
                _state.value = state.value.copy(
                    preferences = _state.value.preferences.copy(
                        listViewType = event.listViewType,
                    ),
                )

                val preferences = _state.value.preferences
                viewModelScope.launch(Dispatchers.IO) {
                    settingsRepository.setPreferences(preferences)
                }
            }

            is TodoListEvent.TopTileEvent.ChangeMoveDoneToPast -> {
                _state.value = state.value.copy(
                    preferences = _state.value.preferences.copy(
                        moveDoneToPast = event.selected,
                    ),
                )

                val preferences = _state.value.preferences
                viewModelScope.launch(Dispatchers.IO) {
                    settingsRepository.setPreferences(preferences)
                }

                loadAllTodos()
            }

            is TodoListEvent.TopTileEvent.ChangeShowOverdueInToday -> {
                _state.value = state.value.copy(
                    preferences = _state.value.preferences.copy(
                        showOverdueInToday = event.selected,
                    ),
                )

                val preferences = _state.value.preferences
                viewModelScope.launch(Dispatchers.IO) {
                    settingsRepository.setPreferences(preferences)
                }

                loadPastTodo()
                loadTodayTodo()
            }

            is TodoListEvent.TopTileEvent.OpenTopTile -> {
                _state.value = state.value.copy(
                    topTileOpen = event.open,
                )
            }
        }
    }

    private fun onEvent(event: TodoListEvent.TimeTileEvent) {
        when (event) {
            is TodoListEvent.TimeTileEvent.ChangeTimePeriod -> {
                _state.value = state.value.copy(
                    timePeriod = event.timePeriod,
                )
            }

            is TodoListEvent.TimeTileEvent.OpenTimeTile -> {
                _state.value = state.value.copy(
                    timeTileOpen = event.open,
                )
            }
        }
    }

    private fun onEvent(event: TodoListEvent.SelectionEvent) {
        when (event) {
            TodoListEvent.SelectionEvent.Check -> {
                val selectedTodos = state.value.selectedTodos.toList()
                viewModelScope.launch(Dispatchers.IO) {
                    todoUseCases.updateTodo.invoke(selectedTodos, true)
                }
                _state.value = state.value.copy(
                    selectedTodos = emptySet(),
                )
            }

            TodoListEvent.SelectionEvent.Delete -> {
                val selectedTodos = state.value.selectedTodos.toList()
                viewModelScope.launch(Dispatchers.IO) {
                    todoUseCases.deleteTodo.invoke(selectedTodos)
                }
                _state.value = state.value.copy(
                    selectedTodos = emptySet(),
                )
            }

            TodoListEvent.SelectionEvent.Deselect -> {
                _state.value = state.value.copy(
                    selectedTodos = emptySet(),
                )
            }

            is TodoListEvent.SelectionEvent.Selected -> {
                val selectedSet = mutableSetOf<Long>()
                selectedSet.addAll(state.value.selectedTodos)
                if (event.id in selectedSet && !event.forceSelect) {
                    selectedSet.remove(event.id)
                } else {
                    selectedSet.add(event.id)
                }
                _state.value = state.value.copy(
                    selectedTodos = selectedSet,
                )
            }

            TodoListEvent.SelectionEvent.Uncheck -> {
                val selectedTodos = state.value.selectedTodos.toList()
                viewModelScope.launch(Dispatchers.IO) {
                    todoUseCases.updateTodo.invoke(selectedTodos, false)
                }
                _state.value = state.value.copy(
                    selectedTodos = emptySet(),
                )
            }

            is TodoListEvent.SelectionEvent.SelectedRange -> {
                val map = when (state.value.timePeriod) {
                    TimePeriod.Future -> state.value.todosFuture
                    TimePeriod.Past -> state.value.todosPast
                    TimePeriod.Today -> state.value.todosToday
                }

                viewModelScope.launch(Dispatchers.IO) {
                    val list = map.flatMap { it.value }
                    val initialIndex =
                        list.indexOfFirst { it.id == event.initial }.takeIf { it != -1 }
                            ?: return@launch
                    val previousIndex =
                        list.indexOfFirst { it.id == event.previous }.takeIf { it != -1 }
                            ?: return@launch
                    val currentIndex =
                        list.indexOfFirst { it.id == event.current }.takeIf { it != -1 }
                            ?: return@launch

                    val selectedSet = mutableSetOf<Long>()
                    selectedSet.addAll(state.value.selectedTodos)
                    val setToRemove =
                        list.subList(
                            min(initialIndex, previousIndex),
                            max(initialIndex, previousIndex) + 1,
                        ).mapNotNull { it.id }.toSet()
                    val setToAdd =
                        list.subList(
                            min(initialIndex, currentIndex),
                            max(initialIndex, currentIndex) + 1,
                        ).mapNotNull { it.id }
                    selectedSet.removeAll(setToRemove)
                    selectedSet.addAll(setToAdd)

                    withContext(Dispatchers.Main) {
                        _state.value = state.value.copy(
                            selectedTodos = selectedSet,
                        )
                    }
                }
            }
        }
    }

    private fun loadAllTodos() {
        loadTodayTodo()
        loadFutureTodo()
        loadPastTodo()
    }

    private fun loadTodayTodo() {
        loadTodayTodosJob?.cancel()

        loadTodayTodosJob = todoUseCases.getTodos(
            GetTodos.TodoTimeType.Today(
                includeDone = !state.value.preferences.moveDoneToPast,
                includeOverdue = state.value.preferences.showOverdueInToday,
            ),
        ).onEach {
            _state.value = state.value.copy(
                todosToday = it,
            )
        }.launchIn(viewModelScope)
    }

    private fun loadFutureTodo() {
        loadFutureTodosJob?.cancel()

        loadFutureTodosJob = todoUseCases.getTodos(
            GetTodos.TodoTimeType.Future(
                includeDone = !state.value.preferences.moveDoneToPast,
            ),
        ).onEach {
            _state.value = state.value.copy(
                todosFuture = it,
            )
        }.launchIn(viewModelScope)
    }

    private fun loadPastTodo() {
        loadPastTodosJob?.cancel()

        loadPastTodosJob = todoUseCases.getTodos(
            GetTodos.TodoTimeType.Past(
                includeDone = state.value.preferences.moveDoneToPast,
                includeOverdue = !state.value.preferences.showOverdueInToday,
            ),
        ).onEach {
            _state.value = state.value.copy(
                todosPast = it,
            )
        }.launchIn(viewModelScope)
    }
}
