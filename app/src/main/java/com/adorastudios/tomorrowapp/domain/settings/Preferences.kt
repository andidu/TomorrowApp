package com.adorastudios.tomorrowapp.domain.settings

import com.adorastudios.tomorrowapp.presentation.screens.todoList.ListViewType

data class Preferences(
    val showOverdueInToday: Boolean = true,
    val moveDoneToPast: Boolean = true,
    val listViewType: ListViewType = ListViewType.TitleAndContent,
)
