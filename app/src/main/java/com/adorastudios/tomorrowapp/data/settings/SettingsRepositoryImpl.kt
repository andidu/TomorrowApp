package com.adorastudios.tomorrowapp.data.settings

import android.content.SharedPreferences
import com.adorastudios.tomorrowapp.domain.settings.Preferences
import com.adorastudios.tomorrowapp.domain.settings.SettingsRepository
import com.adorastudios.tomorrowapp.presentation.screens.todoList.ListViewType
import com.adorastudios.tomorrowapp.presentation.screens.todoList.ListViewType.Companion.toListViewType

class SettingsRepositoryImpl(
    private val sharedPreferences: SharedPreferences,
) : SettingsRepository {
    companion object {
        private const val SHOW_OVERDUE_IN_TODAY = "SHOW_OVERDUE_IN_TODAY"
        private const val MOVE_DONE_TO_PAST = "MOVE_DONE_TO_PAST"
        private const val LIST_VIEW_TYPE = "LIST_VIEW_TYPE"
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
}
