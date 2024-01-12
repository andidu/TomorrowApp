package com.adorastudios.tomorrowapp.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class WidgetTodo(
    val id: Long? = null,
    val title: String,
    val content: String,
    val done: Boolean,
    val dueDate: Int,
    val type: Int,
    val typeDescription: Int?,
    val colorLight: ULong,
    val colorDark: ULong,
) {
    companion object {
        fun Todo.toWidgetTodo() = WidgetTodo(
            id = id,
            title = title,
            content = content,
            done = done,
            dueDate = dueDate,
            type = type.toInt(),
            typeDescription = (type as? TodoType.Defined)?.descriptionId,
            colorLight = color.lightColor.value,
            colorDark = color.darkColor.value,
        )
    }
}
