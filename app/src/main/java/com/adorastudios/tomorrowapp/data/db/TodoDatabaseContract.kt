package com.adorastudios.tomorrowapp.data.db

object TodoDatabaseContract {
    const val DATABASE_NAME = "todos.db"

    object TodoTable {
        const val TABLE_NAME = "todos"

        const val COLUMN_NAME_ID = "id"
        const val COLUMN_NAME_TITLE = "title"
        const val COLUMN_NAME_CONTENT = "content"
        const val COLUMN_NAME_DONE = "done"
        const val COLUMN_NAME_DUE_DATE = "due_date"
        const val COLUMN_NAME_COLOR = "color"
        const val COLUMN_NAME_TYPE = "type"
    }
}
