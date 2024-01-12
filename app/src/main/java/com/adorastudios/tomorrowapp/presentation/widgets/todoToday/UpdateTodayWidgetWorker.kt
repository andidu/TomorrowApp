package com.adorastudios.tomorrowapp.presentation.widgets.todoToday

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.adorastudios.tomorrowapp.domain.currentTime
import com.adorastudios.tomorrowapp.domain.repository.TodoRepository
import com.adorastudios.tomorrowapp.domain.settings.SettingsRepository
import com.adorastudios.tomorrowapp.presentation.widgets.todoToday.TodoTodayWidget.Companion.getTodayWidgetTodosSync
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.runBlocking
import java.util.concurrent.TimeUnit

@HiltWorker
class UpdateTodayWidgetWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val settingsRepository: SettingsRepository,
    private val todoRepository: TodoRepository,
    private val updater: TodoTodayWidgetUpdater,
) : Worker(context, workerParams) {
    override fun doWork(): Result {
        val preferences = settingsRepository.getPreferences()

        runBlocking {
            updater.update(
                data = todoRepository.getTodayWidgetTodosSync(preferences = preferences),
                time = currentTime(),
            )
        }

        context.setUpSemiPeriodicUpdateWidgetWorker(true)

        return Result.success()
    }

    companion object {
        const val TAG = "periodic_update_widget_work"
    }
}

fun Context.setUpSemiPeriodicUpdateWidgetWorker(
    start: Boolean,
) {
    val workManager = WorkManager.getInstance(this)
    if (start) {
        val time = currentTime() / 1000 / 60
        val minutes = (time % 60).toInt()
        val hours = (time / 60 % 24).toInt()

        val delay = ((24 - hours) * 60 - minutes + 1).toLong()

        val work = OneTimeWorkRequestBuilder<UpdateTodayWidgetWorker>()
            .addTag(UpdateTodayWidgetWorker.TAG)
            .setInitialDelay(delay, TimeUnit.MINUTES)
            .build()

        workManager.enqueue(work)
    } else {
        workManager.cancelAllWorkByTag(UpdateTodayWidgetWorker.TAG)
    }
}
