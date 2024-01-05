package com.adorastudios.tomorrowapp.domain.settings

interface SettingsRepository {
    fun getPreferences(): Preferences
    fun setPreferences(preferences: Preferences)
}
