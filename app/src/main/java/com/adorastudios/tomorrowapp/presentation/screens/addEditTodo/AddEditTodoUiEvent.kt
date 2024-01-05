package com.adorastudios.tomorrowapp.presentation.screens.addEditTodo

sealed class AddEditTodoUiEvent {
    data object Back : AddEditTodoUiEvent()
    sealed class Error : AddEditTodoUiEvent() {
        data object EmptyTitle : Error()
    }
}
