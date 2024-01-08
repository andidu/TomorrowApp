package com.adorastudios.tomorrowapp.presentation.screens.notifications

import android.app.AlarmManager
import android.app.Application
import android.content.Context
import android.content.IntentFilter
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.adorastudios.tomorrowapp.domain.notifications.PermissionChecker
import com.adorastudios.tomorrowapp.domain.settings.SettingsRepository
import com.adorastudios.tomorrowapp.presentation.screens.notifications.exactNotifications.ExactNotificationHelper
import com.adorastudios.tomorrowapp.presentation.screens.notifications.exactNotifications.ExactNotificationPermissionListener
import com.adorastudios.tomorrowapp.presentation.screens.notifications.periodicNotifications.setUpPeriodicNotificationWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val application: Application,
    permissionChecker: PermissionChecker,
    private val settingsRepository: SettingsRepository,
    private val exactNotificationHelper: ExactNotificationHelper,
) : AndroidViewModel(application) {
    private val _state: MutableState<NotificationState> =
        mutableStateOf(
            NotificationState(
                notificationTypeInSettings = settingsRepository.getNotificationType(),
                notificationsPermitted = permissionChecker
                    .hasPermission(android.Manifest.permission.POST_NOTIFICATIONS),
                repeatTimePeriodInHours = settingsRepository.getPeriodicPeriod(),
                repeatAtNight = settingsRepository.getRepeatAtNight(),
                exactTimeList = settingsRepository.getExactTimes(),
                exactAllowed = if (android.os.Build.VERSION.SDK_INT >= 34) {
                    (application.getSystemService(Context.ALARM_SERVICE) as? AlarmManager)
                        ?.canScheduleExactAlarms() == true
                } else {
                    true
                },
            ),
        )
    val state: State<NotificationState> = _state

    private val exactNotificationPermissionListener = ExactNotificationPermissionListener {
        viewModelScope.launch(Dispatchers.Main) {
            _state.value = state.value.copy(
                exactAllowed = true,
            )
        }
    }

    init {
        application.registerReceiver(
            exactNotificationPermissionListener,
            IntentFilter().apply {
                addAction(AlarmManager.ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED)
            },
        )
    }

    fun onEvent(event: NotificationEvent) {
        when (event) {
            is NotificationEvent.NotificationTypeChanged -> {
                _state.value = state.value.copy(
                    notificationTypeInSettings = event.notificationType,
                )
                settingsRepository.setNotificationType(event.notificationType)
                when (event.notificationType) {
                    NotificationType.Disabled -> {
                        application.setUpPeriodicNotificationWorker(null)
                        exactNotificationHelper.stopNotifications(state.value.exactTimeList)
                    }

                    NotificationType.Enabled.Exact -> {
                        application.setUpPeriodicNotificationWorker(null)
                        exactNotificationHelper.startNotifications(state.value.exactTimeList)
                    }

                    NotificationType.Enabled.Periodic -> {
                        application.setUpPeriodicNotificationWorker(
                            repeatHours = state.value.repeatTimePeriodInHours,
                        )
                        exactNotificationHelper.stopNotifications(state.value.exactTimeList)
                    }
                }
            }

            is NotificationEvent.NotificationPermission -> {
                _state.value = state.value.copy(
                    notificationsPermitted = event.given,
                )
            }

            is NotificationEvent.NotificationExactChanged.AddExact -> {
                exactNotificationHelper.startNotifications(
                    id = event.id,
                    hour = event.time / 60,
                    minute = event.time % 60,
                )

                val list = state.value.exactTimeList.replace(event.id, event.time)
                _state.value = state.value.copy(exactTimeList = list)

                viewModelScope.launch(Dispatchers.IO) {
                    settingsRepository.setExactTimes(list)
                }
            }

            is NotificationEvent.NotificationExactChanged.DeleteExact -> {
                exactNotificationHelper.stopNotifications(event.index)

                val list = state.value.exactTimeList.replace(event.index, null)
                _state.value = state.value.copy(exactTimeList = list)

                viewModelScope.launch(Dispatchers.IO) {
                    settingsRepository.setExactTimes(list)
                }
            }

            is NotificationEvent.NotificationPeriodicChanged.TimePeriod -> {
                _state.value = state.value.copy(
                    repeatTimePeriodInHours = event.hours,
                )
                application.setUpPeriodicNotificationWorker(event.hours)

                viewModelScope.launch(Dispatchers.IO) {
                    settingsRepository.setPeriodicPeriod(event.hours)
                }
            }

            is NotificationEvent.NotificationPeriodicChanged.SendAtNight -> {
                _state.value = state.value.copy(
                    repeatAtNight = event.boolean,
                )

                viewModelScope.launch(Dispatchers.IO) {
                    settingsRepository.setRepeatAtNight(event.boolean)
                }
            }
        }
    }
}
