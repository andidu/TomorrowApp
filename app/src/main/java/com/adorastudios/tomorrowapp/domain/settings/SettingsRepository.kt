package com.adorastudios.tomorrowapp.domain.settings

import com.adorastudios.tomorrowapp.presentation.screens.notifications.NotificationType
import com.adorastudios.tomorrowapp.presentation.screens.notifications.exactNotifications.SixList

interface SettingsRepository {
    fun getPreferences(): Preferences
    fun setPreferences(preferences: Preferences)

    fun getNotificationType(): NotificationType
    fun setNotificationType(notificationType: NotificationType)

    fun getPeriodicPeriod(): Int
    fun setPeriodicPeriod(period: Int)

    fun getRepeatAtNight(): Boolean
    fun setRepeatAtNight(repeat: Boolean)

    fun getExactTimes(): SixList
    fun setExactTimes(times: SixList)
}
