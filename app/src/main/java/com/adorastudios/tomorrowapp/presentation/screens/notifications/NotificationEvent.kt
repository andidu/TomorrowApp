package com.adorastudios.tomorrowapp.presentation.screens.notifications

sealed class NotificationEvent {
    data class NotificationTypeChanged(val notificationType: NotificationType) : NotificationEvent()
    data class NotificationPermission(val given: Boolean) : NotificationEvent()
    sealed class NotificationPeriodicChanged : NotificationEvent() {
        data class TimePeriod(val hours: Int) : NotificationPeriodicChanged()
        data class SendAtNight(val boolean: Boolean) : NotificationPeriodicChanged()
    }

    sealed class NotificationExactChanged : NotificationEvent() {
        data class AddExact(val id: Int, val time: Int) : NotificationExactChanged()
        data class DeleteExact(val index: Int) : NotificationExactChanged()
    }
}
