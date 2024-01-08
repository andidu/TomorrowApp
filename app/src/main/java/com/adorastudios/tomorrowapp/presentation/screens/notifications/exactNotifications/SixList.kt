package com.adorastudios.tomorrowapp.presentation.screens.notifications.exactNotifications

data class SixList(
    val time1: Int? = null,
    val time2: Int? = null,
    val time3: Int? = null,
    val time4: Int? = null,
    val time5: Int? = null,
    val time6: Int? = null,
) : Iterable<Int?> {

    inner class SixIterator(start: Int, private val endInclusive: Int) : Iterator<Int?> {
        private var initValue = start

        override fun hasNext(): Boolean {
            return initValue <= endInclusive
        }

        override fun next(): Int? {
            return this@SixList.get(initValue++)
        }
    }

    companion object {
        fun String.toSixList(): SixList {
            return split("|")
                .map { it.toIntOrNull() }
                .run {
                    SixList(
                        time1 = getOrNull(0),
                        time2 = getOrNull(1),
                        time3 = getOrNull(2),
                        time4 = getOrNull(3),
                        time5 = getOrNull(4),
                        time6 = getOrNull(5),
                    )
                }
        }
    }

    fun get(index: Int): Int? {
        return when (index) {
            0 -> time1
            1 -> time2
            2 -> time3
            3 -> time4
            4 -> time5
            5 -> time6
            else -> null
        }
    }

    override fun iterator(): Iterator<Int?> {
        return SixIterator(0, 5)
    }

    override fun toString(): String {
        return "$time1|$time2|$time3|$time4|$time5|$time6"
    }

    fun replace(index: Int, value: Int?): SixList {
        return when (index) {
            0 -> copy(time1 = value)
            1 -> copy(time2 = value)
            2 -> copy(time3 = value)
            3 -> copy(time4 = value)
            4 -> copy(time5 = value)
            5 -> copy(time6 = value)
            else -> copy()
        }
    }
}
