package com.adorastudios.tomorrowapp.presentation.widgets.todoToday

import com.adorastudios.tomorrowapp.domain.model.WidgetTodo

interface TodoTodayWidgetUpdater {
    suspend fun update(data: List<WidgetTodo>, time: Long)
}
