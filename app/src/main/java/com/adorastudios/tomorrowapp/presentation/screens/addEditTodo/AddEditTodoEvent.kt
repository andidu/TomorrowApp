package com.adorastudios.tomorrowapp.presentation.screens.addEditTodo

import com.adorastudios.tomorrowapp.domain.model.TodoColor
import com.adorastudios.tomorrowapp.domain.model.TodoType

sealed class AddEditTodoEvent {
    data class TitleChanged(val title: String) : AddEditTodoEvent()
    data class ContentChanged(val content: String) : AddEditTodoEvent()
    data object SaveTodo : AddEditTodoEvent()
    data object DeleteTodo : AddEditTodoEvent()
    data class ColorChanged(val color: TodoColor) : AddEditTodoEvent()
    data class TypeChanged(val type: TodoType) : AddEditTodoEvent()
}
