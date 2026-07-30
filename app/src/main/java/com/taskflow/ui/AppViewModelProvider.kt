package com.taskflow.ui

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.taskflow.TaskFlowApp
import com.taskflow.ui.calendar.CalendarViewModel
import com.taskflow.ui.dashboard.DashboardViewModel
import com.taskflow.ui.tasks.TaskEditViewModel
import com.taskflow.ui.tasks.TaskListViewModel

/**
 * 모든 화면의 ViewModel 생성을 담당하는 단일 팩토리.
 */
object AppViewModelProvider {

    val Factory = viewModelFactory {
        initializer {
            DashboardViewModel(app().taskRepository)
        }
        initializer {
            TaskListViewModel(app().taskRepository, app().reminderScheduler)
        }
        initializer {
            TaskEditViewModel(
                repository = app().taskRepository,
                scheduler = app().reminderScheduler,
                savedStateHandle = createSavedStateHandle(),
            )
        }
        initializer {
            CalendarViewModel(app().taskRepository, app().reminderScheduler)
        }
    }

    private fun CreationExtras.app(): TaskFlowApp =
        this[APPLICATION_KEY] as TaskFlowApp
}
