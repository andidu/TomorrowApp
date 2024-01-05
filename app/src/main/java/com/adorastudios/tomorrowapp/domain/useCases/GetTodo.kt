package com.adorastudios.tomorrowapp.domain.useCases

import com.adorastudios.tomorrowapp.domain.model.Todo
import com.adorastudios.tomorrowapp.domain.repository.TodoRepository

class GetTodo(private val todoRepository: TodoRepository) {
    suspend operator fun invoke(id: Int): Todo? {
        return todoRepository.getTodoById(id)
    }
}
