package com.adorastudios.tomorrowapp.domain.useCases

import com.adorastudios.tomorrowapp.domain.repository.TodoRepository

class DeleteTodo(private val todoRepository: TodoRepository) {
    suspend operator fun invoke(id: Long) {
        todoRepository.deleteTodo(id)
    }
}
