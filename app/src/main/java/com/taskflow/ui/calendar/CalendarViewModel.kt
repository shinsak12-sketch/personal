package com.taskflow.ui.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taskflow.data.local.Task
import com.taskflow.data.repository.TaskRepository
import com.taskflow.notification.ReminderScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.YearMonth

data class CalendarUiState(
    val yearMonth: YearMonth = YearMonth.now(),
    val selectedDate: LocalDate = LocalDate.now(),
    val countsByDate: Map<LocalDate, DayInfo> = emptyMap(),
    val selectedTasks: List<Task> = emptyList(),
    val monthTotal: Int = 0,
    val monthDone: Int = 0,
) {
    val selectedDone: Int get() = selectedTasks.count { it.isCompleted }
}

/** 특정 날짜의 전체/완료 건수와 대표 카테고리 색(최대 3개). */
data class DayInfo(
    val total: Int,
    val done: Int,
    val categoryColors: List<Long>,
)

private data class CalendarControls(
    val yearMonth: YearMonth,
    val selectedDate: LocalDate,
)

class CalendarViewModel(
    private val repository: TaskRepository,
    private val scheduler: ReminderScheduler,
) : ViewModel() {

    private val controls = MutableStateFlow(
        CalendarControls(YearMonth.now(), LocalDate.now()),
    )

    val uiState: StateFlow<CalendarUiState> =
        combine(repository.tasks, controls) { tasks, control ->
            val counts = tasks
                .filter { it.dueDate != null }
                .groupBy { it.dueDate!! }
                .mapValues { (_, list) ->
                    val colors = list
                        .groupingBy { it.category }
                        .eachCount()
                        .entries
                        .sortedByDescending { it.value }
                        .map { it.key.colorArgb }
                        .take(3)
                    DayInfo(total = list.size, done = list.count { it.isCompleted }, categoryColors = colors)
                }

            val selectedTasks = tasks
                .filter { it.dueDate == control.selectedDate }
                .sortedWith(
                    compareByDescending<Task> { !it.isCompleted }
                        .thenBy { it.dueTime ?: LocalTime.MAX }
                        .thenByDescending { it.priority.weight },
                )

            val monthTasks = tasks.filter {
                it.dueDate != null && YearMonth.from(it.dueDate) == control.yearMonth
            }

            CalendarUiState(
                yearMonth = control.yearMonth,
                selectedDate = control.selectedDate,
                countsByDate = counts,
                selectedTasks = selectedTasks,
                monthTotal = monthTasks.size,
                monthDone = monthTasks.count { it.isCompleted },
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = CalendarUiState(),
        )

    fun nextMonth() = controls.update { it.copy(yearMonth = it.yearMonth.plusMonths(1)) }

    fun previousMonth() = controls.update { it.copy(yearMonth = it.yearMonth.minusMonths(1)) }

    fun selectDate(date: LocalDate) = controls.update {
        it.copy(selectedDate = date, yearMonth = YearMonth.from(date))
    }

    fun goToToday() = controls.update {
        CalendarControls(YearMonth.now(), LocalDate.now())
    }

    fun toggleComplete(task: Task, completed: Boolean) {
        viewModelScope.launch {
            val updated = task.copy(
                isCompleted = completed,
                completedAt = if (completed) LocalDateTime.now() else null,
            )
            repository.update(updated)
            if (completed) scheduler.cancel(task.id) else scheduler.schedule(updated)
        }
    }
}
