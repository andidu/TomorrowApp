package com.adorastudios.tomorrowapp

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.adorastudios.tomorrowapp.data.db.TodoDatabase
import com.adorastudios.tomorrowapp.data.db.TodoDatabaseContract
import com.adorastudios.tomorrowapp.data.repository.TodoRepositoryImpl
import com.adorastudios.tomorrowapp.data.settings.SettingsRepositoryImpl
import com.adorastudios.tomorrowapp.domain.repository.TodoRepository
import com.adorastudios.tomorrowapp.domain.settings.SettingsRepository
import com.adorastudios.tomorrowapp.domain.useCases.DeleteTodo
import com.adorastudios.tomorrowapp.domain.useCases.GetTodo
import com.adorastudios.tomorrowapp.domain.useCases.GetTodos
import com.adorastudios.tomorrowapp.domain.useCases.InsertTodo
import com.adorastudios.tomorrowapp.domain.useCases.TodoUseCases
import com.adorastudios.tomorrowapp.domain.useCases.UpdateTodo
import com.adorastudios.tomorrowapp.presentation.widgets.todoToday.TodoTodayWidgetUpdater
import com.adorastudios.tomorrowapp.presentation.widgets.todoToday.TodoTodayWidgetUpdaterImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HiltAppModule {
    @Provides
    @Singleton
    fun provideTodoDatabase(app: Application): TodoDatabase {
        return Room.databaseBuilder(
            app,
            TodoDatabase::class.java,
            TodoDatabaseContract.DATABASE_NAME,
        ).allowMainThreadQueries().build()
    }

    @Provides
    @Singleton
    fun provideTodoRepository(db: TodoDatabase): TodoRepository {
        return TodoRepositoryImpl(db.todoDao)
    }

    @Provides
    @Singleton
    fun provideTodoUseCases(repository: TodoRepository): TodoUseCases {
        return TodoUseCases(
            insertTodo = InsertTodo(repository),
            updateTodo = UpdateTodo(repository),
            deleteTodo = DeleteTodo(repository),
            getTodo = GetTodo(repository),
            getTodos = GetTodos(repository),
        )
    }

    @Provides
    @Singleton
    fun provideSettingsRepository(application: Application): SettingsRepository {
        return SettingsRepositoryImpl(
            application.getSharedPreferences(
                "settings",
                Context.MODE_PRIVATE,
            ),
        )
    }

    @Provides
    @Singleton
    fun bindTodoTodayWidgetUpdater(application: Application): TodoTodayWidgetUpdater {
        return TodoTodayWidgetUpdaterImpl(application)
    }
}
