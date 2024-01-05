package com.adorastudios.tomorrowapp.domain.useCases

import com.adorastudios.tomorrowapp.domain.currentDay
import com.adorastudios.tomorrowapp.domain.model.Todo
import com.adorastudios.tomorrowapp.domain.repository.TodoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.SortedMap

class GetTodos(private val todoRepository: TodoRepository) {
    sealed class TodoTimeType(val day: Int) {
        data class Past(
            val includeDone: Boolean,
            val includeOverdue: Boolean,
        ) : TodoTimeType(currentDay())

        data class Today(
            val includeDone: Boolean,
            val includeOverdue: Boolean,
        ) : TodoTimeType(currentDay())

        data class Future(
            val includeDone: Boolean,
        ) : TodoTimeType(currentDay())
    }

    operator fun invoke(todoTimeType: TodoTimeType): Flow<SortedMap<Int, List<Todo>>> {
        return when (todoTimeType) {
            is TodoTimeType.Future -> {
                todoRepository.getNextTodos(
                    day = todoTimeType.day,
                    includeDone = todoTimeType.includeDone,
                )
                    .map { list -> orderNext(list) }
            }

            is TodoTimeType.Past -> {
                todoRepository.getPrevTodos(
                    day = todoTimeType.day,
                    includeDone = todoTimeType.includeDone,
                    includeOverdue = todoTimeType.includeOverdue,
                ).map { list -> orderPrev(list) }
            }

            is TodoTimeType.Today -> {
                todoRepository.getTodayTodos(
                    day = todoTimeType.day,
                    includeDone = todoTimeType.includeDone,
                    includeOverdue = todoTimeType.includeOverdue,
                ).map { list -> orderToday(list) }
            }
        }
    }

    private fun orderNext(list: List<Todo>): SortedMap<Int, List<Todo>> {
        return list.groupBy { it.dueDate }.toSortedMap(compareBy { it })
    }

    private fun orderPrev(list: List<Todo>): SortedMap<Int, List<Todo>> {
        return list.groupBy { it.dueDate }.toSortedMap(compareByDescending { it })
    }

    private fun orderToday(list: List<Todo>): SortedMap<Int, List<Todo>> {
        return list.groupBy { it.dueDate }.toSortedMap(compareBy { it })
    }
}
