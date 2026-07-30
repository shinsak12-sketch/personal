package com.taskflow.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.taskflow.data.local.Task
import java.time.LocalTime
import java.time.ZoneId

/**
 * AlarmManager 로 할 일 알림을 예약/취소한다.
 * 시각이 지정되지 않은 경우 오전 9시를 기본 알림 시각으로 사용한다.
 */
class ReminderScheduler(private val context: Context) {

    private val alarmManager: AlarmManager =
        context.getSystemService(AlarmManager::class.java)

    fun schedule(task: Task) {
        if (!task.reminderEnabled || task.isCompleted || task.dueDate == null) return

        val time = task.dueTime ?: LocalTime.of(9, 0)
        val triggerAtMillis = task.dueDate.atTime(time)
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        // 이미 지난 시각이면 예약하지 않는다.
        if (triggerAtMillis <= System.currentTimeMillis()) return

        val pendingIntent = buildPendingIntent(task)
        try {
            if (canScheduleExact()) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent,
                )
            } else {
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent,
                )
            }
        } catch (e: SecurityException) {
            // 정확 알람 권한이 없으면 비정확 알람으로 폴백한다.
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent,
            )
        }
    }

    fun cancel(taskId: Long) {
        alarmManager.cancel(buildCancelIntent(taskId))
    }

    /** 저장 시 호출: 알림 상태에 맞춰 재예약 또는 취소한다. */
    fun reschedule(task: Task) {
        cancel(task.id)
        schedule(task)
    }

    private fun canScheduleExact(): Boolean =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            alarmManager.canScheduleExactAlarms()
        } else {
            true
        }

    private fun buildPendingIntent(task: Task): PendingIntent {
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            action = ReminderReceiver.ACTION_REMIND
            putExtra(ReminderReceiver.EXTRA_TASK_ID, task.id)
            putExtra(ReminderReceiver.EXTRA_TITLE, task.title)
            putExtra(ReminderReceiver.EXTRA_TEXT, task.description.ifBlank { "예정된 일정입니다." })
        }
        return PendingIntent.getBroadcast(
            context,
            task.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    private fun buildCancelIntent(taskId: Long): PendingIntent {
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            action = ReminderReceiver.ACTION_REMIND
        }
        return PendingIntent.getBroadcast(
            context,
            taskId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }
}
