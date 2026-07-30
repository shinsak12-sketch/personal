package com.taskflow.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * 예약된 알람이 울릴 때 호출되어 알림을 표시한다.
 */
class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != ACTION_REMIND) return
        val taskId = intent.getLongExtra(EXTRA_TASK_ID, 0L)
        val title = intent.getStringExtra(EXTRA_TITLE) ?: "할 일 알림"
        val text = intent.getStringExtra(EXTRA_TEXT) ?: "예정된 일정입니다."
        NotificationHelper.showReminder(context, taskId, title, text)
    }

    companion object {
        const val ACTION_REMIND = "com.taskflow.action.REMIND"
        const val EXTRA_TASK_ID = "extra_task_id"
        const val EXTRA_TITLE = "extra_title"
        const val EXTRA_TEXT = "extra_text"
    }
}
