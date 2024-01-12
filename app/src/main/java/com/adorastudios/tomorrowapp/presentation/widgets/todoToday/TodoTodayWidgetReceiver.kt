package com.adorastudios.tomorrowapp.presentation.widgets.todoToday

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver

class TodoTodayWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = TodoTodayWidget()

    override fun onEnabled(context: Context?) {
        super.onEnabled(context)

        context ?: return
        context.setUpSemiPeriodicUpdateWidgetWorker(true)
    }

    override fun onDisabled(context: Context?) {
        super.onDisabled(context)

        context ?: return
        context.setUpSemiPeriodicUpdateWidgetWorker(false)
    }
}
