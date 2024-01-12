package com.adorastudios.tomorrowapp.presentation.widgets.todoToday

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import com.adorastudios.tomorrowapp.domain.model.WidgetTodo
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class TodoTodayWidgetUpdaterImpl(
    private val context: Context,
) : TodoTodayWidgetUpdater {
    override suspend fun update(data: List<WidgetTodo>, time: Long) {
        GlanceAppWidgetManager(context).getGlanceIds(TodoTodayWidget::class.java)
            .forEach { glanceId ->
                updateAppWidgetState(context, glanceId) { prefs ->
                    prefs[TodoTodayWidget.todoTodayWidgetTodosKey] =
                        data.map { Json.encodeToString(it) }.toSet()
                    prefs[TodoTodayWidget.todoTodayWidgetTimeKey] = time
                }

                TodoTodayWidget().update(context, glanceId)
            }
    }
}
