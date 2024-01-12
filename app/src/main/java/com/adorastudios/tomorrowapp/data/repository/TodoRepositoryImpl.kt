package com.adorastudios.tomorrowapp.data.repository

import com.adorastudios.tomorrowapp.data.db.TodoDao
import com.adorastudios.tomorrowapp.data.db.TodoDb.Companion.toTodoDb
import com.adorastudios.tomorrowapp.domain.model.Todo
import com.adorastudios.tomorrowapp.domain.repository.TodoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TodoRepositoryImpl(private val dao: TodoDao) : TodoRepository {
    override fun getTodayTodos(
        day: Int,
        includeDone: Boolean,
        includeOverdue: Boolean,
    ): Flow<List<Todo>> {
        return dao.getTodayTodos(day, includeDone, includeOverdue).map { list ->
            list.map { it.toTodo() }
        }
    }

    override fun getPrevTodos(
        day: Int,
        includeDone: Boolean,
        includeOverdue: Boolean,
    ): Flow<List<Todo>> {
        return dao.getPrevTodos(day, includeDone, includeOverdue).map { list ->
            list.map { it.toTodo() }
        }
    }

    override fun getNextTodos(
        day: Int,
        includeDone: Boolean,
    ): Flow<List<Todo>> {
        return dao.getNextTodos(day, includeDone).map { list ->
            list.map { it.toTodo() }
        }
    }

    override suspend fun getTodoById(id: Int): Todo? {
        return dao.getTodoById(id)?.toTodo()
    }

    override suspend fun insertTodo(todo: Todo) {
        dao.insertTodo(todo.toTodoDb())
    }

    override suspend fun updateTodos(ids: List<Long>, done: Boolean) {
        dao.updateTodos(ids, done)
    }

    override suspend fun deleteTodo(id: Long) {
        dao.deleteTodo(id)
    }

    override suspend fun deleteTodos(ids: List<Long>) {
        dao.deleteTodos(ids)
    }

    override fun updateTodosSync(ids: List<Long>, done: Boolean) {
        dao.updateTodosSync(ids, done)
    }

    override fun getTodayTodosSync(
        day: Int,
        includeDone: Boolean,
        includeOverdue: Boolean,
    ): List<Todo> {
        return dao.getTodayTodosSync(day, includeDone, includeOverdue).map { it.toTodo() }
    }
}
