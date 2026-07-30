package com.taskflow.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.taskflow.data.model.Category
import com.taskflow.data.model.Priority
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

/**
 * 할 일 / 일정 한 건을 표현하는 Room 엔티티이자 앱 전반에서 쓰는 도메인 모델.
 *
 * @param dueDate 마감/예정 날짜. null 이면 날짜 미지정(언젠가 할 일).
 * @param dueTime 예정 시각. null 이면 하루 종일 항목.
 * @param reminderEnabled true 이면 dueDate/dueTime 기준으로 알림을 예약한다.
 */
@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String = "",
    val category: Category = Category.WORK,
    val priority: Priority = Priority.MEDIUM,
    val dueDate: LocalDate? = null,
    val dueTime: LocalTime? = null,
    val isCompleted: Boolean = false,
    val reminderEnabled: Boolean = false,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val completedAt: LocalDateTime? = null,
) {
    /** dueDate + dueTime 을 합친 마감 일시. 시각이 없으면 그 날 자정 기준. */
    val dueDateTime: LocalDateTime?
        get() = dueDate?.let { LocalDateTime.of(it, dueTime ?: LocalTime.MIDNIGHT) }
}
