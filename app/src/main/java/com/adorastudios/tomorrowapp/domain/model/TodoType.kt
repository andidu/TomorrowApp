package com.adorastudios.tomorrowapp.domain.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AirplanemodeActive
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.Work
import androidx.compose.ui.graphics.vector.ImageVector
import com.adorastudios.tomorrowapp.R

sealed class TodoType {

    sealed class Defined(
        val icon: ImageVector,
        val descriptionId: Int,
    ) : TodoType() {
        data object Work : Defined(
            Icons.Rounded.Work,
            R.string.contentDescription_work,
        )

        data object Study : Defined(
            Icons.Rounded.School,
            R.string.contentDescription_study,
        )

        data object Travel : Defined(
            Icons.Rounded.AirplanemodeActive,
            R.string.contentDescription_travel,
        )

        data object Home : Defined(
            Icons.Rounded.Home,
            R.string.contentDescription_home,
        )

        data object Health : Defined(
            Icons.Rounded.Favorite,
            R.string.contentDescription_favorite,
        )
    }

    data object Undefined : TodoType()

    fun toInt(): Int {
        return when (this) {
            Defined.Work -> 1
            Defined.Study -> 2
            Defined.Travel -> 3
            Defined.Home -> 4
            Defined.Health -> 5
            Undefined -> 0
        }
    }

    companion object {
        fun Int.toTodoType(): TodoType {
            return when (this) {
                1 -> Defined.Work
                2 -> Defined.Study
                3 -> Defined.Travel
                4 -> Defined.Home
                5 -> Defined.Health
                else -> Undefined
            }
        }
    }
}
