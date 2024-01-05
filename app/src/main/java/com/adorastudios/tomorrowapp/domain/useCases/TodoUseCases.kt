package com.adorastudios.tomorrowapp.domain.useCases

data class TodoUseCases(
    val insertTodo: InsertTodo,
    val deleteTodo: DeleteTodo,
    val getTodo: GetTodo,
    val getTodos: GetTodos,
)
