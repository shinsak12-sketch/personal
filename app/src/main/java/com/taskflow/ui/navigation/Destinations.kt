package com.taskflow.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.SpaceDashboard
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * 하단 네비게이션 최상위 화면들. 순서 = 탭 표시 순서(캘린더가 첫 화면).
 */
enum class TopLevelDestination(
    val route: String,
    val label: String,
    val icon: ImageVector,
) {
    CALENDAR("calendar", "캘린더", Icons.Filled.CalendarMonth),
    TASKS("tasks", "할 일", Icons.Filled.Checklist),
    DASHBOARD("dashboard", "대시보드", Icons.Filled.SpaceDashboard),
}

object Routes {
    const val DASHBOARD = "dashboard"
    const val TASKS = "tasks"
    const val CALENDAR = "calendar"

    /** taskId 가 -1 이면 새 항목. date 가 비어있지 않으면 새 항목의 기본 날짜(ISO). */
    const val TASK_EDIT = "task_edit?taskId={taskId}&date={date}"
    const val TASK_EDIT_ARG = "taskId"
    const val TASK_EDIT_DATE_ARG = "date"

    fun taskEdit(taskId: Long = -1L, date: String = ""): String =
        "task_edit?taskId=$taskId&date=$date"
}
