package com.adorastudios.tomorrowapp.presentation.screens.notifications

import com.adorastudios.tomorrowapp.presentation.screens.notifications.exactNotifications.SixList
import javax.annotation.concurrent.Immutable

@Immutable
data class NotificationState(
    val notificationTypeInSettings: NotificationType,
    val notificationsPermitted: Boolean,
    val repeatTimePeriodInHours: Int = 6,
    val repeatAtNight: Boolean = false,
    val exactAllowed: Boolean = false,
    val exactTimeList: SixList = SixList(),
)

sealed class NotificationType {
    data object Disabled : NotificationType()
    sealed class Enabled : NotificationType() {
        data object Exact : Enabled()
        data object Periodic : Enabled()
    }

    fun toInt() = when (this) {
        Disabled -> 0
        Enabled.Exact -> 1
        Enabled.Periodic -> 2
    }

    companion object {
        fun Int.toNotificationType() = when (this) {
            1 -> Enabled.Exact
            2 -> Enabled.Periodic
            else -> Disabled
        }
    }
}
