package com.adorastudios.tomorrowapp.presentation.screens.notifications.periodicNotifications

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.adorastudios.tomorrowapp.domain.currentDay
import com.adorastudios.tomorrowapp.domain.currentTime
import com.adorastudios.tomorrowapp.domain.repository.TodoRepository
import com.adorastudios.tomorrowapp.domain.settings.SettingsRepository
import com.adorastudios.tomorrowapp.presentation.screens.notifications.sendNotification
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit

@HiltWorker
class PeriodicNotificationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val settingsRepository: SettingsRepository,
    private val todoRepository: TodoRepository,
) : Worker(context, workerParams) {
    override fun doWork(): Result {
        val preferences = settingsRepository.getPreferences()
        val sendAtNight = settingsRepository.getRepeatAtNight()
        val time = currentTime()
        if (!sendAtNight && time / 1000 / 60 / 60 % 24 in 1..8) {
            return Result.failure()
        }

        val todos = todoRepository.getTodayTodosSync(
            day = currentDay(),
            includeDone = preferences.moveDoneToPast,
            includeOverdue = preferences.showOverdueInToday,
        )

        val amount = todos.size
        val random3 = todos.shuffled().take(3)

        applicationContext.sendNotification(
            amount = amount,
            random3 = random3,
        )

        return Result.success()
    }

    companion object {
        const val TAG = "periodic_notification_work"
        const val NAME = "periodic_notification_periodic_work"
    }
}

fun Context.setUpPeriodicNotificationWorker(
    repeatHours: Int?,
) {
    val workManager = WorkManager.getInstance(this)
    if (repeatHours != null) {
        val time = currentTime() / 1000 / 60
        val minutes = (time % 60).toInt()
        val hours = (time / 60 % 24).toInt()

        var nextHour = repeatHours
        while (nextHour <= hours) {
            nextHour += repeatHours
        }

        val delay = ((nextHour - hours) * 60 - minutes).toLong()

        val work = PeriodicWorkRequestBuilder<PeriodicNotificationWorker>(
            repeatInterval = repeatHours.toLong(),
            repeatIntervalTimeUnit = TimeUnit.HOURS,
            flexTimeInterval = 15,
            flexTimeIntervalUnit = TimeUnit.MINUTES,
        )
            .addTag(PeriodicNotificationWorker.TAG)
            .setInitialDelay(delay, TimeUnit.MINUTES)
            .build()

        workManager.enqueueUniquePeriodicWork(
            PeriodicNotificationWorker.NAME,
            ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
            work,
        )
    } else {
        workManager.cancelAllWorkByTag(PeriodicNotificationWorker.TAG)
    }
}
