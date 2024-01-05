package com.adorastudios.tomorrowapp.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.adorastudios.tomorrowapp.domain.model.Todo
import com.adorastudios.tomorrowapp.domain.model.TodoColor.Companion.toTodoColor
import com.adorastudios.tomorrowapp.domain.model.TodoType.Companion.toTodoType

@Entity(
    tableName = TodoDatabaseContract.TodoTable.TABLE_NAME,
)
data class TodoDb(
    @ColumnInfo(name = TodoDatabaseContract.TodoTable.COLUMN_NAME_ID)
    @PrimaryKey
    val id: Long? = null,

    @ColumnInfo(name = TodoDatabaseContract.TodoTable.COLUMN_NAME_TITLE)
    val title: String,

    @ColumnInfo(name = TodoDatabaseContract.TodoTable.COLUMN_NAME_CONTENT)
    val content: String,

    @ColumnInfo(name = TodoDatabaseContract.TodoTable.COLUMN_NAME_DONE)
    val done: Boolean,

    @ColumnInfo(name = TodoDatabaseContract.TodoTable.COLUMN_NAME_DUE_DATE)
    val dueDate: Int,

    @ColumnInfo(name = TodoDatabaseContract.TodoTable.COLUMN_NAME_TYPE)
    val type: Int,

    @ColumnInfo(name = TodoDatabaseContract.TodoTable.COLUMN_NAME_COLOR)
    val color: Int,
) {
    fun toTodo(): Todo {
        return Todo(
            id = id,
            title = title,
            content = content,
            done = done,
            dueDate = dueDate,
            type = type.toTodoType(),
            color = color.toTodoColor(),
        )
    }

    companion object {
        fun Todo.toTodoDb(): TodoDb {
            return TodoDb(
                id = id,
                title = title,
                content = content,
                done = done,
                dueDate = dueDate,
                type = type.toInt(),
                color = color.toInt(),
            )
        }
    }
}
