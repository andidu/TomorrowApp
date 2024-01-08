package com.adorastudios.tomorrowapp.presentation.screens

sealed class Screens(val route: String) {
    data object TodoList : Screens("todo_list")
    data object AddEditTodo : Screens("add_edit_todo")
    data object Notifications : Screens("notifications")
    companion object {
        fun toAddEditScreen(id: Int = -1) = AddEditTodo.route + "?todoId=" + id
    }
}
