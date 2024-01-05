@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package com.adorastudios.tomorrowapp.presentation.screens.addEditTodo

import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adorastudios.tomorrowapp.domain.MILLIS_TO_DAYS
import com.adorastudios.tomorrowapp.domain.currentDay
import com.adorastudios.tomorrowapp.domain.model.Todo
import com.adorastudios.tomorrowapp.domain.model.TodoColor
import com.adorastudios.tomorrowapp.domain.model.TodoType
import com.adorastudios.tomorrowapp.domain.useCases.TodoUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditTodoViewModel @Inject constructor(
    private val todoUseCases: TodoUseCases,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val _title = mutableStateOf(String())
    val title: State<String> = _title
    private val _content = mutableStateOf(String())
    val content: State<String> = _content
    private val _color: MutableState<TodoColor> = mutableStateOf(TodoColor.Transparent)
    val color: State<TodoColor> = _color
    private val _type: MutableState<TodoType> = mutableStateOf(TodoType.Undefined)
    val type: State<TodoType> = _type
    val date: DatePickerState by mutableStateOf(
        DatePickerState(
            initialSelectedDateMillis = null,
            initialDisplayedMonthMillis = null,
            yearRange = DatePickerDefaults.YearRange,
            initialDisplayMode = DisplayMode.Picker,
        ),
    )

    private var currentId: Long? = null
    private var currentDone: Boolean = false

    private val _eventFlow = MutableSharedFlow<AddEditTodoUiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        savedStateHandle.get<Int>("todoId")?.let { todoId ->
            if (todoId != -1) {
                viewModelScope.launch {
                    todoUseCases.getTodo(todoId)?.also { todo ->
                        currentId = todo.id
                        currentDone = todo.done
                        date.setSelection(todo.dueDate.toLong() * MILLIS_TO_DAYS)
                        _title.value = todo.title
                        _content.value = todo.content
                        _color.value = todo.color
                        _type.value = todo.type
                    }
                }
            } else {
                date.setSelection((currentDay() + 1).toLong() * MILLIS_TO_DAYS)
            }
        }
    }

    fun onEvent(event: AddEditTodoEvent) {
        when (event) {
            is AddEditTodoEvent.TitleChanged -> {
                _title.value = event.title
            }

            is AddEditTodoEvent.ContentChanged -> {
                _content.value = event.content
            }

            AddEditTodoEvent.SaveTodo -> {
                viewModelScope.launch {
                    if (title.value.isEmpty()) {
                        _eventFlow.emit(AddEditTodoUiEvent.Error.EmptyTitle)
                    } else {
                        todoUseCases.insertTodo(
                            Todo(
                                id = currentId,
                                title = title.value,
                                content = content.value,
                                done = currentDone,
                                color = color.value,
                                type = type.value,
                                dueDate = date.selectedDateMillis?.div(MILLIS_TO_DAYS)?.toInt()
                                    ?: currentDay(),
                            ),
                        )
                        _eventFlow.emit(AddEditTodoUiEvent.Back)
                    }
                }
            }

            AddEditTodoEvent.DeleteTodo -> {
                viewModelScope.launch {
                    currentId?.let { currentId ->
                        todoUseCases.deleteTodo(id = currentId)
                    }
                    _eventFlow.emit(AddEditTodoUiEvent.Back)
                }
            }

            is AddEditTodoEvent.ColorChanged -> {
                _color.value = event.color
            }

            is AddEditTodoEvent.TypeChanged -> {
                _type.value = event.type
            }
        }
    }
}
