package com.taskflow.data.local

import androidx.room.TypeConverter
import com.taskflow.data.model.Category
import com.taskflow.data.model.Priority
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

/**
 * Room 이 지원하지 않는 타입(java.time, enum)을 문자열로 직렬화/역직렬화한다.
 */
class Converters {
    @TypeConverter fun fromLocalDate(value: LocalDate?): String? = value?.toString()
    @TypeConverter fun toLocalDate(value: String?): LocalDate? = value?.let(LocalDate::parse)

    @TypeConverter fun fromLocalTime(value: LocalTime?): String? = value?.toString()
    @TypeConverter fun toLocalTime(value: String?): LocalTime? = value?.let(LocalTime::parse)

    @TypeConverter fun fromLocalDateTime(value: LocalDateTime?): String? = value?.toString()
    @TypeConverter fun toLocalDateTime(value: String?): LocalDateTime? = value?.let(LocalDateTime::parse)

    @TypeConverter fun fromCategory(value: Category): String = value.name
    @TypeConverter fun toCategory(value: String): Category = Category.valueOf(value)

    @TypeConverter fun fromPriority(value: Priority): String = value.name
    @TypeConverter fun toPriority(value: String): Priority = Priority.valueOf(value)
}
