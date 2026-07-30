package com.taskflow.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taskflow.data.local.Task
import com.taskflow.data.model.Category
import com.taskflow.data.repository.TaskRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate

data class DayProgress(val date: LocalDate, val done: Int, val total: Int)

data class CategorySlice(val category: Category, val count: Int)

data class DashboardUiState(
    val today: LocalDate = LocalDate.now(),
    val todayTasks: List<Task> = emptyList(),
    val overdue: List<Task> = emptyList(),
    val todayDone: Int = 0,
    val todayTotal: Int = 0,
    val activeTotal: Int = 0,
    val completedTotal: Int = 0,
    val weekly: List<DayProgress> = emptyList(),
    val categories: List<CategorySlice> = emptyList(),
) {
    val todayRate: Float get() = if (todayTotal == 0) 0f else todayDone.toFloat() / todayTotal
}

class DashboardViewModel(repository: TaskRepository) : ViewModel() {

    val uiState: StateFlow<DashboardUiState> = repository.tasks
        .map { tasks -> buildState(tasks) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = DashboardUiState(),
        )

    private fun buildState(tasks: List<Task>): DashboardUiState {
        val today = LocalDate.now()

        val todayTasks = tasks
            .filter { it.dueDate == today }
            .sortedWith(taskComparator)

        val overdue = tasks
            .filter { !it.isCompleted && it.dueDate != null && it.dueDate < today }
            .sortedWith(taskComparator)

        val active = tasks.filter { !it.isCompleted }

        val weekly = (6 downTo 0).map { offset ->
            val date = today.minusDays(offset.toLong())
            val ofDay = tasks.filter { it.dueDate == date }
            DayProgress(date = date, done = ofDay.count { it.isCompleted }, total = ofDay.size)
        }

        val categories = active
            .groupingBy { it.category }
            .eachCount()
            .map { (category, count) -> CategorySlice(category, count) }
            .sortedByDescending { it.count }

        return DashboardUiState(
            today = today,
            todayTasks = todayTasks,
            overdue = overdue,
            todayDone = todayTasks.count { it.isCompleted },
            todayTotal = todayTasks.size,
            activeTotal = active.size,
            completedTotal = tasks.count { it.isCompleted },
            weekly = weekly,
            categories = categories,
        )
    }

    private val taskComparator = compareByDescending<Task> { !it.isCompleted }
        .thenByDescending { it.priority.weight }
        .thenBy { it.dueTime ?: java.time.LocalTime.MAX }
}
