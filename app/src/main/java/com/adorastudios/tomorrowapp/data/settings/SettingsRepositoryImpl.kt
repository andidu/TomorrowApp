package com.adorastudios.tomorrowapp.data.settings

import android.content.SharedPreferences
import com.adorastudios.tomorrowapp.domain.settings.Preferences
import com.adorastudios.tomorrowapp.domain.settings.SettingsRepository
import com.adorastudios.tomorrowapp.presentation.screens.notifications.NotificationType
import com.adorastudios.tomorrowapp.presentation.screens.notifications.NotificationType.Companion.toNotificationType
import com.adorastudios.tomorrowapp.presentation.screens.notifications.exactNotifications.SixList
import com.adorastudios.tomorrowapp.presentation.screens.notifications.exactNotifications.SixList.Companion.toSixList
import com.adorastudios.tomorrowapp.presentation.screens.todoList.ListViewType
import com.adorastudios.tomorrowapp.presentation.screens.todoList.ListViewType.Companion.toListViewType

class SettingsRepositoryImpl(
    private val sharedPreferences: SharedPreferences,
) : SettingsRepository {
    companion object {
        private const val SHOW_OVERDUE_IN_TODAY = "SHOW_OVERDUE_IN_TODAY"
        private const val MOVE_DONE_TO_PAST = "MOVE_DONE_TO_PAST"
        private const val LIST_VIEW_TYPE = "LIST_VIEW_TYPE"
        private const val NOTIFICATION_TYPE = "NOTIFICATION_TYPE"
        private const val PERIODIC_NOTIFICATION_PERIOD = "PERIODIC_NOTIFICATION_PERIOD"
        private const val REPEAT_AT_NIGHT = "REPEAT_AT_NIGHT"
        private const val EXACT_NOTIFICATION_TIMES = "EXACT_NOTIFICATION_TIMES"
    }

    override fun getPreferences(): Preferences {
        return Preferences(
            showOverdueInToday = sharedPreferences.getBoolean(SHOW_OVERDUE_IN_TODAY, true),
            moveDoneToPast = sharedPreferences.getBoolean(MOVE_DONE_TO_PAST, true),
            listViewType = sharedPreferences.getInt(
                LIST_VIEW_TYPE,
                ListViewType.TitleAndContent.toInt(),
            ).toListViewType(),
        )
    }

    override fun setPreferences(preferences: Preferences) {
        sharedPreferences
            .edit()
            .putBoolean(SHOW_OVERDUE_IN_TODAY, preferences.showOverdueInToday)
            .putBoolean(MOVE_DONE_TO_PAST, preferences.moveDoneToPast)
            .putInt(LIST_VIEW_TYPE, preferences.listViewType.toInt())
            .apply()
    }

    override fun getNotificationType(): NotificationType {
        return sharedPreferences
            .getInt(NOTIFICATION_TYPE, NotificationType.Disabled.toInt())
            .toNotificationType()
    }

    override fun setNotificationType(notificationType: NotificationType) {
        sharedPreferences
            .edit()
            .putInt(NOTIFICATION_TYPE, notificationType.toInt())
            .apply()
    }

    override fun getPeriodicPeriod(): Int {
        return sharedPreferences.getInt(PERIODIC_NOTIFICATION_PERIOD, 6)
    }

    override fun setPeriodicPeriod(period: Int) {
        sharedPreferences
            .edit()
            .putInt(PERIODIC_NOTIFICATION_PERIOD, period)
            .apply()
    }

    override fun getRepeatAtNight(): Boolean {
        return sharedPreferences.getBoolean(REPEAT_AT_NIGHT, false)
    }

    override fun setRepeatAtNight(repeat: Boolean) {
        sharedPreferences
            .edit()
            .putBoolean(REPEAT_AT_NIGHT, false)
            .apply()
    }

    override fun getExactTimes(): SixList {
        return (sharedPreferences.getString(EXACT_NOTIFICATION_TIMES, "") ?: "").toSixList()
    }

    override fun setExactTimes(times: SixList) {
        sharedPreferences
            .edit()
            .putString(EXACT_NOTIFICATION_TIMES, times.toString())
            .apply()
    }
}
