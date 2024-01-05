package com.adorastudios.tomorrowapp.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [TodoDb::class],
    version = 1,
)
abstract class TodoDatabase : RoomDatabase() {
    abstract val todoDao: TodoDao
}
