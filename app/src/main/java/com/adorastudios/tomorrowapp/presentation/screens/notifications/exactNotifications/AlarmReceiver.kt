package com.adorastudios.tomorrowapp.presentation.screens.notifications.exactNotifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.adorastudios.tomorrowapp.domain.currentDay
import com.adorastudios.tomorrowapp.domain.repository.TodoRepository
import com.adorastudios.tomorrowapp.domain.settings.SettingsRepository
import com.adorastudios.tomorrowapp.presentation.screens.notifications.sendNotification
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var settingsRepository: SettingsRepository

    @Inject
    lateinit var todoRepository: TodoRepository

    @Inject
    lateinit var remindersManager: RemindersManager

    override fun onReceive(context: Context, intent: Intent) {
        val hour = intent.getIntExtra(RemindersManager.STRING_HOUR, 9)
        val minute = intent.getIntExtra(RemindersManager.STRING_MINUTE, 0)
        val id = intent.getIntExtra(RemindersManager.STRING_ID, 0)
        val preferences = settingsRepository.getPreferences()
        val todos = todoRepository.getTodayTodosSync(
            day = currentDay(),
            includeDone = preferences.moveDoneToPast,
            includeOverdue = preferences.showOverdueInToday,
        )

        val amount = todos.size
        val random3 = todos.shuffled().take(3)

        context.sendNotification(
            amount = amount,
            random3 = random3,
        )

        remindersManager.startReminder(
            hour = hour,
            minute = minute,
            reminderId = id,
        )
    }
}
