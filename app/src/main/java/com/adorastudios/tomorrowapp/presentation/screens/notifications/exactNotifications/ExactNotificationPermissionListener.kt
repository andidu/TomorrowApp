package com.adorastudios.tomorrowapp.presentation.screens.notifications.exactNotifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ExactNotificationPermissionListener(
    private val action: () -> Unit,
) : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        action()
    }
}
