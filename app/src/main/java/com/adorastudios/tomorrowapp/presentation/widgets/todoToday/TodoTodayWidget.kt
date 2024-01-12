package com.adorastudios.tomorrowapp.presentation.widgets.todoToday

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.color.DayNightColorProvider
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import com.adorastudios.tomorrowapp.MainActivity
import com.adorastudios.tomorrowapp.R
import com.adorastudios.tomorrowapp.domain.currentDay
import com.adorastudios.tomorrowapp.domain.currentTime
import com.adorastudios.tomorrowapp.domain.getDate
import com.adorastudios.tomorrowapp.domain.getTime
import com.adorastudios.tomorrowapp.domain.model.TodoType.Companion.toTodoIcon
import com.adorastudios.tomorrowapp.domain.model.WidgetTodo
import com.adorastudios.tomorrowapp.domain.model.WidgetTodo.Companion.toWidgetTodo
import com.adorastudios.tomorrowapp.domain.repository.TodoRepository
import com.adorastudios.tomorrowapp.domain.settings.SettingsRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json

class TodoTodayWidget : GlanceAppWidget() {
    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface TodoEntryPoint {
        fun todoRepository(): TodoRepository
        fun settingsRepository(): SettingsRepository
        fun widgetUpdater(): TodoTodayWidgetUpdater
    }

    override var stateDefinition: GlanceStateDefinition<*> = PreferencesGlanceStateDefinition

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val appContext = context.applicationContext ?: throw IllegalStateException()

        val entryPoint =
            EntryPointAccessors.fromApplication<TodoEntryPoint>(context = appContext)

        val todoRepository = entryPoint.todoRepository()
        val settingsRepository = entryPoint.settingsRepository()

        val preferences = settingsRepository.getPreferences()
        val defaultList = todoRepository.getTodayWidgetTodosSync(preferences)
        val defaultTime = currentTime()

        provideContent {
            val prefs = currentState<Preferences>()

            val todoList = prefs[todoTodayWidgetTodosKey]?.map {
                Json.decodeFromString<WidgetTodo>(it)
            }?.sortedBy { it.dueDate } ?: defaultList
            val time = prefs[todoTodayWidgetTimeKey] ?: defaultTime

            GlanceTheme {
                TodoWidgetContent(
                    list = todoList,
                    time = time,
                    onSelected = {
                        it.id?.let { id ->
                            runBlocking(Dispatchers.IO) {
                                todoRepository.updateTodosSync(listOf(id), !it.done)
                                val widgetUpdater = entryPoint.widgetUpdater()
                                val data = todoRepository.getTodayWidgetTodosSync(preferences)
                                widgetUpdater.update(data, currentTime())
                            }
                        }
                    },
                    onUpdate = {
                        runBlocking(Dispatchers.IO) {
                            val widgetUpdater = entryPoint.widgetUpdater()
                            val data = todoRepository.getTodayWidgetTodosSync(preferences)
                            widgetUpdater.update(data, currentTime())
                        }
                    },
                )
            }
        }
    }

    companion object {
        val todoTodayWidgetTodosKey = stringSetPreferencesKey("todos_key")
        val todoTodayWidgetTimeKey = longPreferencesKey("time_key")

        fun TodoRepository.getTodayWidgetTodosSync(
            preferences: com.adorastudios.tomorrowapp.domain.settings.Preferences,
        ): List<WidgetTodo> {
            return this.getTodayTodosSync(
                day = currentDay(),
                includeDone = !preferences.moveDoneToPast,
                includeOverdue = preferences.showOverdueInToday,
            ).map { it.toWidgetTodo() }
        }
    }
}

@Composable
fun TodoWidgetContent(
    list: List<WidgetTodo>,
    onSelected: (WidgetTodo) -> Unit,
    time: Long,
    onUpdate: () -> Unit,
) {
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .clickable(actionStartActivity<MainActivity>())
            .background(GlanceTheme.colors.background),
    ) {
        Text(
            modifier = GlanceModifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            text = stringResource(id = R.string.widget_todayTasks),
            style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = GlanceTheme.colors.primary,
                textAlign = TextAlign.Center,
            ),
        )
        LazyColumn(
            modifier = GlanceModifier
                .fillMaxWidth()
                .defaultWeight()
                .padding(horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            items(list) { item ->
                Item(todo = item, onSelected)
            }
        }
        Row(
            modifier = GlanceModifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Spacer(modifier = GlanceModifier.width(12.dp))
            Text(
                text = time.getDate(),
                style = TextStyle(
                    fontSize = 12.sp,
                    color = GlanceTheme.colors.onBackground,
                    textAlign = TextAlign.Center,
                ),
            )
            Spacer(modifier = GlanceModifier.defaultWeight())
            Text(
                text = time.getTime(),
                style = TextStyle(
                    fontSize = 12.sp,
                    color = GlanceTheme.colors.onBackground,
                    textAlign = TextAlign.Center,
                ),
            )
            Spacer(modifier = GlanceModifier.width(8.dp))
            Image(
                modifier = GlanceModifier
                    .size(16.dp)
                    .cornerRadius(1000.dp)
                    .clickable { onUpdate() },
                provider = ImageProvider(R.drawable.round_update_24),
                contentDescription = stringResource(id = R.string.contentDescription_update),
                colorFilter = ColorFilter.tint(GlanceTheme.colors.onBackground),
            )
            Spacer(modifier = GlanceModifier.width(8.dp))
        }
    }
}

@Composable
fun Item(
    todo: WidgetTodo,
    onSelected: (WidgetTodo) -> Unit,
) {
    Box(
        modifier = GlanceModifier
            .cornerRadius(8.dp)
            .padding(8.dp),
    ) {
        val color = DayNightColorProvider(
            day = Color(todo.colorLight),
            night = Color(todo.colorDark),
        )
        Row(
            modifier = GlanceModifier
                .fillMaxWidth()
                .cornerRadius(8.dp)
                .background(color)
                .padding(8.dp, 4.dp),
            verticalAlignment = Alignment.Vertical.CenterVertically,
        ) {
            when (val type = todo.type.toTodoIcon()) {
                null -> {
                    Box(modifier = GlanceModifier.size(30.dp)) {}
                }

                else -> {
                    Box(
                        modifier = GlanceModifier.size(30.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Image(
                            provider = ImageProvider(type),
                            contentDescription = todo.typeDescription?.let { stringResource(id = it) },
                            colorFilter = ColorFilter.tint(GlanceTheme.colors.onBackground),
                        )
                    }
                }
            }
            Spacer(modifier = GlanceModifier.width(8.dp))
            Text(
                text = todo.title,
                maxLines = 1,
                modifier = GlanceModifier.defaultWeight(),
                style = TextStyle(
                    color = GlanceTheme.colors.onBackground,
                ),
            )
            Spacer(modifier = GlanceModifier.width(8.dp))
            Image(
                modifier = GlanceModifier
                    .size(30.dp)
                    .cornerRadius(1000.dp)
                    .clickable {
                        onSelected(todo)
                    },
                provider = ImageProvider(if (todo.done) R.drawable.round_check_circle_outline_24 else R.drawable.round_radio_button_unchecked_24),
                contentDescription = stringResource(id = if (todo.done) R.string.contentDescription_done else R.string.contentDescription_notDone),
                colorFilter = ColorFilter.tint(GlanceTheme.colors.onBackground),
            )
        }
    }
}

@Composable
@ReadOnlyComposable
fun stringResource(@StringRes id: Int, vararg formatArgs: Any): String {
    val context = LocalContext.current
    return context.getString(id, *formatArgs)
}
