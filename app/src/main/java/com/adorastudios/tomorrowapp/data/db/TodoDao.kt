package com.adorastudios.tomorrowapp.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {
    companion object {
        private const val TODAY_QUERY = "SELECT * FROM todos WHERE (due_date=:day AND done=0) OR " +
            "(:includeDone AND due_date=:day AND done=1) OR " +
            "(:includeOverdue AND due_date<:day AND done=0)"

        private const val UPDATE_QUERY = "UPDATE todos SET done=:done WHERE id IN (:ids)"
    }

    @Query(TODAY_QUERY)
    fun getTodayTodos(day: Int, includeDone: Boolean, includeOverdue: Boolean): Flow<List<TodoDb>>

    @Query(TODAY_QUERY)
    fun getTodayTodosSync(day: Int, includeDone: Boolean, includeOverdue: Boolean): List<TodoDb>

    @Query(
        "SELECT * FROM todos WHERE (due_date<:day AND done=1) OR " +
            "(:includeDone AND done=1) OR " +
            "(:includeOverdue AND due_date<:day)",
    )
    fun getPrevTodos(day: Int, includeDone: Boolean, includeOverdue: Boolean): Flow<List<TodoDb>>

    @Query(
        "SELECT * FROM todos WHERE (due_date>:day AND done=0) OR " +
            "(:includeDone AND due_date>:day AND done=1)",
    )
    fun getNextTodos(day: Int, includeDone: Boolean): Flow<List<TodoDb>>

    @Query("SELECT * FROM todos WHERE id = :id")
    suspend fun getTodoById(id: Int): TodoDb?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTodo(todo: TodoDb)

    @Query(UPDATE_QUERY)
    suspend fun updateTodos(ids: List<Long>, done: Boolean)

    @Query("DELETE FROM todos WHERE id = :id")
    suspend fun deleteTodo(id: Long)

    @Query("DELETE FROM todos WHERE id IN (:ids)")
    suspend fun deleteTodos(ids: List<Long>)

    @Query(UPDATE_QUERY)
    fun updateTodosSync(ids: List<Long>, done: Boolean)

    @Query("SELECT title FROM todos WHERE due_date <= :day AND done = 0")
    fun getNotFinishedTodoTitles(day: Long): List<String>
}
