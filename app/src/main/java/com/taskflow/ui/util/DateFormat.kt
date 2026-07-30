package com.taskflow.ui.util

import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.util.Locale

private val timeFormatter = DateTimeFormatter.ofPattern("a h:mm", Locale.KOREAN)
private val dateFormatter = DateTimeFormatter.ofPattern("M월 d일 (E)", Locale.KOREAN)
private val monthTitleFormatter = DateTimeFormatter.ofPattern("yyyy년 M월", Locale.KOREAN)

fun LocalTime.toKorean(): String = format(timeFormatter)

fun LocalDate.toKorean(): String = format(dateFormatter)

fun LocalDate.toMonthTitle(): String = format(monthTitleFormatter)

fun LocalDate.dayOfWeekShort(): String =
    dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.KOREAN)

/**
 * 오늘 기준으로 사람이 읽기 좋은 상대 표현("오늘", "내일", "3일 지남" 등)을 만든다.
 */
fun LocalDate.relativeLabel(today: LocalDate = LocalDate.now()): String {
    val days = ChronoUnit.DAYS.between(today, this)
    return when {
        days == 0L -> "오늘"
        days == 1L -> "내일"
        days == -1L -> "어제"
        days in 2..6 -> "${days}일 후"
        days < -1 -> "${-days}일 지남"
        else -> toKorean()
    }
}
