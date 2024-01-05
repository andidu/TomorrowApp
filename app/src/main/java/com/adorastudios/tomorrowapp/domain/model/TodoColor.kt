package com.adorastudios.tomorrowapp.domain.model

import androidx.compose.ui.graphics.Color
import com.adorastudios.tomorrowapp.ui.theme.BlueNoteDark
import com.adorastudios.tomorrowapp.ui.theme.BlueNoteLight
import com.adorastudios.tomorrowapp.ui.theme.GreenNoteDark
import com.adorastudios.tomorrowapp.ui.theme.GreenNoteLight
import com.adorastudios.tomorrowapp.ui.theme.PurpleNoteDark
import com.adorastudios.tomorrowapp.ui.theme.PurpleNoteLight
import com.adorastudios.tomorrowapp.ui.theme.RedNoteDark
import com.adorastudios.tomorrowapp.ui.theme.RedNoteLight
import com.adorastudios.tomorrowapp.ui.theme.YellowNoteDark
import com.adorastudios.tomorrowapp.ui.theme.YellowNoteLight

sealed class TodoColor(
    val lightColor: Color,
    val darkColor: Color,
) {
    data object Red : TodoColor(
        lightColor = RedNoteLight,
        darkColor = RedNoteDark,
    )

    data object Yellow : TodoColor(
        lightColor = YellowNoteLight,
        darkColor = YellowNoteDark,
    )

    data object Green : TodoColor(
        lightColor = GreenNoteLight,
        darkColor = GreenNoteDark,
    )

    data object Blue : TodoColor(
        lightColor = BlueNoteLight,
        darkColor = BlueNoteDark,
    )

    data object Purple : TodoColor(
        lightColor = PurpleNoteLight,
        darkColor = PurpleNoteDark,
    )

    data object Transparent : TodoColor(
        lightColor = Color.Transparent,
        darkColor = Color.Transparent,
    )

    fun toInt(): Int {
        return when (this) {
            Red -> 1
            Yellow -> 2
            Green -> 3
            Blue -> 4
            Purple -> 5
            Transparent -> 0
        }
    }

    fun get(dark: Boolean) = if (dark) darkColor else lightColor

    companion object {
        fun Int.toTodoColor(): TodoColor {
            return when (this) {
                1 -> Red
                2 -> Yellow
                3 -> Green
                4 -> Blue
                5 -> Purple
                else -> Transparent
            }
        }
    }
}
