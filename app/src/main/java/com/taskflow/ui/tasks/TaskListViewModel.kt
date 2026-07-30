package com.taskflow.ui.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taskflow.data.local.Task
import com.taskflow.data.model.Category
import com.taskflow.data.repository.TaskRepository
import com.taskflow.notification.ReminderScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.LocalTime

enum class SortMode(val label: String) {
    DUE_DATE("마감일순"),
    PRIORITY("우선순위순"),
    CREATED("등록순"),
}

data class TaskFilter(
    val category: Category? = null,
    val showCompleted: Boolean = true,
    val sort: SortMode = SortMode.DUE_DATE,
)

data class TaskListUiState(
    val tasks: List<Task> = emptyList(),
    val filter: TaskFilter = TaskFilter(),
    val activeCount: Int = 0,
    val completedCount: Int = 0,
)

class TaskListViewModel(
    private val repository: TaskRepository,
    private val scheduler: ReminderScheduler,
) : ViewModel() {

    private val filter = MutableStateFlow(TaskFilter())

    val uiState: StateFlow<TaskListUiState> =
        combine(repository.tasks, filter) { tasks, currentFilter ->
            val filtered = tasks
                .asSequence()
                .filter { currentFilter.category == null || it.category == currentFilter.category }
                .filter { currentFilter.showCompleted || !it.isCompleted }
                .sortedWith(comparatorFor(currentFilter.sort))
                .toList()

            TaskListUiState(
                tasks = filtered,
                filter = currentFilter,
                activeCount = tasks.count { !it.isCompleted },
                completedCount = tasks.count { it.isCompleted },
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = TaskListUiState(),
        )

    fun setCategory(category: Category?) {
        filter.value = filter.value.copy(category = category)
    }

    fun toggleShowCompleted() {
        filter.value = filter.value.copy(showCompleted = !filter.value.showCompleted)
    }

    fun setSort(sort: SortMode) {
        filter.value = filter.value.copy(sort = sort)
    }

    fun toggleComplete(task: Task, completed: Boolean) {
        viewModelScope.launch {
            val updated = task.copy(
                isCompleted = completed,
                completedAt = if (completed) LocalDateTime.now() else null,
            )
            repository.update(updated)
            if (completed) {
                scheduler.cancel(task.id)
            } else {
                scheduler.schedule(updated)
            }
        }
    }

    fun delete(task: Task) {
        viewModelScope.launch {
            scheduler.cancel(task.id)
            repository.delete(task)
        }
    }

    private fun comparatorFor(sort: SortMode): Comparator<Task> {
        val incompleteFirst = compareByDescending<Task> { !it.isCompleted }
        return when (sort) {
            SortMode.DUE_DATE -> incompleteFirst
                .thenBy { it.dueDate ?: java.time.LocalDate.MAX }
                .thenBy { it.dueTime ?: LocalTime.MAX }
                .thenByDescending { it.priority.weight }

            SortMode.PRIORITY -> incompleteFirst
                .thenByDescending { it.priority.weight }
                .thenBy { it.dueDate ?: java.time.LocalDate.MAX }

            SortMode.CREATED -> incompleteFirst
                .thenByDescending { it.createdAt }
        }
    }
}
