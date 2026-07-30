package com.taskflow.ui.tasks

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taskflow.data.local.Task
import com.taskflow.data.model.Category
import com.taskflow.data.model.Priority
import com.taskflow.data.repository.TaskRepository
import com.taskflow.notification.ReminderScheduler
import com.taskflow.ui.navigation.Routes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

data class TaskEditUiState(
    val id: Long = 0L,
    val title: String = "",
    val description: String = "",
    val category: Category = Category.WORK,
    val priority: Priority = Priority.MEDIUM,
    val dueDate: LocalDate? = null,
    val dueTime: LocalTime? = null,
    val reminderEnabled: Boolean = false,
    val isCompleted: Boolean = false,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val completedAt: LocalDateTime? = null,
    val isEditing: Boolean = false,
    val finished: Boolean = false,
) {
    val canSave: Boolean get() = title.isNotBlank()
}

class TaskEditViewModel(
    private val repository: TaskRepository,
    private val scheduler: ReminderScheduler,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val taskId: Long = savedStateHandle.get<Long>(Routes.TASK_EDIT_ARG) ?: -1L

    private val _uiState = MutableStateFlow(TaskEditUiState())
    val uiState: StateFlow<TaskEditUiState> = _uiState.asStateFlow()

    init {
        if (taskId != -1L) {
            viewModelScope.launch {
                repository.getById(taskId)?.let { task ->
                    _uiState.value = TaskEditUiState(
                        id = task.id,
                        title = task.title,
                        description = task.description,
                        category = task.category,
                        priority = task.priority,
                        dueDate = task.dueDate,
                        dueTime = task.dueTime,
                        reminderEnabled = task.reminderEnabled,
                        isCompleted = task.isCompleted,
                        createdAt = task.createdAt,
                        completedAt = task.completedAt,
                        isEditing = true,
                    )
                }
            }
        }
    }

    fun onTitleChange(value: String) = _uiState.update { it.copy(title = value) }
    fun onDescriptionChange(value: String) = _uiState.update { it.copy(description = value) }
    fun onCategoryChange(value: Category) = _uiState.update { it.copy(category = value) }
    fun onPriorityChange(value: Priority) = _uiState.update { it.copy(priority = value) }

    fun onDateChange(value: LocalDate?) = _uiState.update {
        // 날짜가 없으면 알림도 끈다.
        it.copy(dueDate = value, reminderEnabled = it.reminderEnabled && value != null)
    }

    fun onTimeChange(value: LocalTime?) = _uiState.update { it.copy(dueTime = value) }

    fun onReminderChange(enabled: Boolean) = _uiState.update { it.copy(reminderEnabled = enabled) }

    fun save() {
        val state = _uiState.value
        if (!state.canSave) return
        viewModelScope.launch {
            val task = Task(
                id = state.id,
                title = state.title.trim(),
                description = state.description.trim(),
                category = state.category,
                priority = state.priority,
                dueDate = state.dueDate,
                dueTime = state.dueTime,
                isCompleted = state.isCompleted,
                reminderEnabled = state.reminderEnabled && state.dueDate != null,
                createdAt = state.createdAt,
                completedAt = state.completedAt,
            )
            val savedId = repository.save(task)
            scheduler.reschedule(task.copy(id = savedId))
            _uiState.update { it.copy(finished = true) }
        }
    }

    fun delete() {
        val state = _uiState.value
        viewModelScope.launch {
            if (state.isEditing) {
                scheduler.cancel(state.id)
                repository.deleteById(state.id)
            }
            _uiState.update { it.copy(finished = true) }
        }
    }
}
