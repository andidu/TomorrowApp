package com.adorastudios.tomorrowapp.domain.useCases

import com.adorastudios.tomorrowapp.domain.repository.TodoRepository

class UpdateTodo(private val todoRepository: TodoRepository) {
    suspend operator fun invoke(ids: List<Long>, done: Boolean) {
        todoRepository.updateTodos(ids, done)
    }
}
