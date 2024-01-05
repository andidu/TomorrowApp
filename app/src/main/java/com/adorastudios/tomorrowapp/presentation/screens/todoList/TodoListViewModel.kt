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
                            done = event.done,
                        ),
                    )
                }
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
