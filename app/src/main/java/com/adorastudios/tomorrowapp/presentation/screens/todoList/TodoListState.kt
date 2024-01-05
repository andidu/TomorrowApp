package com.adorastudios.tomorrowapp.presentation.screens.todoList

import com.adorastudios.tomorrowapp.domain.model.Todo
import com.adorastudios.tomorrowapp.domain.settings.Preferences
import javax.annotation.concurrent.Immutable

@Immutable
data class TodoListState(
    val todosToday: Map<Int, List<Todo>> = emptyMap(),
    val todosPast: Map<Int, List<Todo>> = emptyMap(),
    val todosFuture: Map<Int, List<Todo>> = emptyMap(),
    val timePeriod: TimePeriod = TimePeriod.Today,
    val preferences: Preferences = Preferences(),
    val topTileOpen: Boolean = false,
    val timeTileOpen: Boolean = false,
)

sealed class TimePeriod {
    data object Today : TimePeriod()
    data object Past : TimePeriod()
    data object Future : TimePeriod()
}

sealed class ListViewType {
    data object TitleOnly : ListViewType()
    data object TitleAndContent : ListViewType()
    data object LargeGrid : ListViewType()
    data object SmallGrid : ListViewType()

    fun toInt() = when (this) {
        LargeGrid -> 2
        SmallGrid -> 3
        TitleAndContent -> 1
        TitleOnly -> 0
    }

    companion object {
        fun Int.toListViewType() = when (this) {
            0 -> TitleOnly
            2 -> LargeGrid
            3 -> SmallGrid
            else -> TitleAndContent
        }
    }
}
