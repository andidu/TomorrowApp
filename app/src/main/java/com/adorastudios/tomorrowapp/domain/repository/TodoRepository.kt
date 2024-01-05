package com.adorastudios.tomorrowapp.domain.repository

import com.adorastudios.tomorrowapp.domain.model.Todo
import kotlinx.coroutines.flow.Flow

interface TodoRepository {
    fun getTodayTodos(day: Int, includeDone: Boolean, includeOverdue: Boolean): Flow<List<Todo>>

    fun getPrevTodos(day: Int, includeDone: Boolean, includeOverdue: Boolean): Flow<List<Todo>>

    fun getNextTodos(day: Int, includeDone: Boolean): Flow<List<Todo>>

    suspend fun getTodoById(id: Int): Todo?

    suspend fun insertTodo(todo: Todo)

    suspend fun deleteTodo(id: Long)
}
