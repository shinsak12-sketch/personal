package com.taskflow.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.taskflow.data.local.AppDatabase
import com.taskflow.data.repository.TaskRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * 기기 재부팅 시 예약된 알람이 모두 사라지므로, 알림이 켜진 미완료 항목을 다시 예약한다.
 */
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED &&
            intent.action != Intent.ACTION_LOCKED_BOOT_COMPLETED
        ) {
            return
        }

        val pendingResult = goAsync()
        val appContext = context.applicationContext
        val repository = TaskRepository(AppDatabase.getInstance(appContext).taskDao())
        val scheduler = ReminderScheduler(appContext)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                repository.getRemindable().forEach { scheduler.schedule(it) }
            } finally {
                pendingResult.finish()
            }
        }
    }
}
