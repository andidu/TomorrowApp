package com.adorastudios.tomorrowapp.presentation.screens.notifications.exactNotifications

import javax.inject.Inject

class ExactNotificationHelper @Inject constructor(
    private val remindersManager: RemindersManager,
) {
    fun startNotifications(
        id: Int,
        hour: Int,
        minute: Int,
    ) {
        remindersManager.startReminder(
            hour = hour,
            minute = minute,
            reminderId = id,
        )
    }

    fun startNotifications(
        sixList: SixList,
    ) {
        sixList.forEachIndexed { id, value ->
            if (value != null) {
                remindersManager.startReminder(
                    hour = value / 60,
                    minute = value % 60,
                    reminderId = id,
                )
            }
        }
    }

    fun stopNotifications(id: Int) {
        remindersManager.stopReminder(id)
    }

    fun stopNotifications(sixList: SixList) {
        sixList.forEachIndexed { index, _ ->
            remindersManager.stopReminder(index)
        }
    }
}
