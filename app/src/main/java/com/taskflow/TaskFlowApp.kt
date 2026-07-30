package com.taskflow

import android.app.Application
import com.taskflow.data.local.AppDatabase
import com.taskflow.data.repository.TaskRepository
import com.taskflow.notification.NotificationHelper
import com.taskflow.notification.ReminderScheduler

/**
 * 앱 전역 의존성을 보관하는 간이 서비스 로케이터.
 * (규모가 작아 Hilt 대신 수동 주입을 사용한다.)
 */
class TaskFlowApp : Application() {

    val taskRepository: TaskRepository by lazy {
        TaskRepository(AppDatabase.getInstance(this).taskDao())
    }

    val reminderScheduler: ReminderScheduler by lazy {
        ReminderScheduler(this)
    }

    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createChannel(this)
    }
}
