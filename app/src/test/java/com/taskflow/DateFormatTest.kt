package com.taskflow

import com.taskflow.ui.util.relativeLabel
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class DateFormatTest {

    private val today = LocalDate.of(2026, 7, 30)

    @Test
    fun today_returnsOneul() {
        assertEquals("오늘", today.relativeLabel(today))
    }

    @Test
    fun tomorrow_returnsNaeil() {
        assertEquals("내일", today.plusDays(1).relativeLabel(today))
    }

    @Test
    fun yesterday_returnsEoje() {
        assertEquals("어제", today.minusDays(1).relativeLabel(today))
    }

    @Test
    fun withinWeek_returnsDaysLater() {
        assertEquals("3일 후", today.plusDays(3).relativeLabel(today))
    }

    @Test
    fun overdue_returnsDaysPast() {
        assertEquals("5일 지남", today.minusDays(5).relativeLabel(today))
    }
}
