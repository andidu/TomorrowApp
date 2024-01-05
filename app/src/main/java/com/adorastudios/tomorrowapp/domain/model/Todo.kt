package com.adorastudios.tomorrowapp.domain.model

data class Todo(
    val id: Long? = null,
    val title: String,
    val content: String,
    val done: Boolean,
    val dueDate: Int,
    val type: TodoType,
    val color: TodoColor,
)
