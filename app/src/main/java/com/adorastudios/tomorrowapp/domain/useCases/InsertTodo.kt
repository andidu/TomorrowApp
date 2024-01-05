package com.adorastudios.tomorrowapp.domain.useCases

import com.adorastudios.tomorrowapp.domain.model.Todo
import com.adorastudios.tomorrowapp.domain.repository.TodoRepository

class InsertTodo(private val todoRepository: TodoRepository) {
    suspend operator fun invoke(todo: Todo) {
        todoRepository.insertTodo(todo)
    }
}
