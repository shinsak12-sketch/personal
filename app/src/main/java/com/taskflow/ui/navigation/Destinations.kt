package com.taskflow.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.SpaceDashboard
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * 하단 네비게이션에 노출되는 최상위 화면들.
 */
enum class TopLevelDestination(
    val route: String,
    val label: String,
    val icon: ImageVector,
) {
    DASHBOARD("dashboard", "대시보드", Icons.Filled.SpaceDashboard),
    TASKS("tasks", "할 일", Icons.Filled.Checklist),
    CALENDAR("calendar", "캘린더", Icons.Filled.CalendarMonth),
}

object Routes {
    const val DASHBOARD = "dashboard"
    const val TASKS = "tasks"
    const val CALENDAR = "calendar"

    /** taskId 가 -1 이면 새 항목 작성. */
    const val TASK_EDIT = "task_edit?taskId={taskId}"
    const val TASK_EDIT_ARG = "taskId"

    fun taskEdit(taskId: Long = -1L): String = "task_edit?taskId=$taskId"
}
