package com.adorastudios.tomorrowapp.presentation.screens.todoList

import com.adorastudios.tomorrowapp.domain.model.Todo

sealed class TodoListEvent {
    sealed class TopTileEvent : TodoListEvent() {
        data class OpenTopTile(val open: Boolean) : TopTileEvent()
        data class ChangeListViewType(val listViewType: ListViewType) : TopTileEvent()
        data class ChangeShowOverdueInToday(val selected: Boolean) : TopTileEvent()
        data class ChangeMoveDoneToPast(val selected: Boolean) : TopTileEvent()
    }

    sealed class TimeTileEvent : TodoListEvent() {
        data class OpenTimeTile(val open: Boolean) : TimeTileEvent()
        data class ChangeTimePeriod(val timePeriod: TimePeriod) : TimeTileEvent()
    }

    sealed class TodoEvent : TodoListEvent() {
        data class Done(val todo: Todo, val done: Boolean) : TodoEvent()
    }
}
