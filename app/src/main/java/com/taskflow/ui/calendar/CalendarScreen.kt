package com.taskflow.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.taskflow.ui.AppViewModelProvider
import com.taskflow.ui.components.EmptyState
import com.taskflow.ui.components.TaskRow
import com.taskflow.ui.util.toKorean
import com.taskflow.ui.util.toMonthTitle
import java.time.DayOfWeek
import java.time.LocalDate

private val weekdayLabels = listOf("일", "월", "화", "수", "목", "금", "토")

@Composable
fun CalendarScreen(
    onAddTask: (LocalDate) -> Unit,
    onOpenTask: (Long) -> Unit,
    viewModel: CalendarViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            MonthHeader(
                title = state.yearMonth.atDay(1).toMonthTitle(),
                onPrev = viewModel::previousMonth,
                onNext = viewModel::nextMonth,
                onToday = viewModel::goToToday,
            )
        }
        item {
            MonthGrid(
                state = state,
                onSelect = viewModel::selectDate,
            )
        }
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "${state.selectedDate.toKorean()} 일정",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "${state.selectedTasks.size}건",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        if (state.selectedTasks.isEmpty()) {
            item {
                EmptyState(emoji = "🗓️", message = "이 날은 등록된 일정이 없습니다.")
            }
        } else {
            items(state.selectedTasks, key = { it.id }) { task ->
                TaskRow(
                    task = task,
                    onToggleComplete = { checked -> viewModel.toggleComplete(task, checked) },
                    onClick = { onOpenTask(task.id) },
                )
            }
        }
        item { Spacer(Modifier.height(24.dp)) }
    }
}

@Composable
private fun MonthHeader(
    title: String,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    onToday: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f),
        )
        IconButton(onClick = onToday) {
            Icon(Icons.Filled.Today, contentDescription = "오늘")
        }
        IconButton(onClick = onPrev) {
            Icon(Icons.Filled.ChevronLeft, contentDescription = "이전 달")
        }
        IconButton(onClick = onNext) {
            Icon(Icons.Filled.ChevronRight, contentDescription = "다음 달")
        }
    }
}

@Composable
private fun MonthGrid(
    state: CalendarUiState,
    onSelect: (LocalDate) -> Unit,
) {
    val yearMonth = state.yearMonth
    val firstDay = yearMonth.atDay(1)
    // 일요일 시작. DayOfWeek: MON=1..SUN=7 → 일요일 기준 오프셋 계산.
    val leadingEmpty = firstDay.dayOfWeek.value % 7
    val daysInMonth = yearMonth.lengthOfMonth()
    val today = LocalDate.now()

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                weekdayLabels.forEachIndexed { index, label ->
                    Text(
                        text = label,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelSmall,
                        color = when (index) {
                            0 -> Color(0xFFE53935)
                            6 -> Color(0xFF1E88E5)
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        },
                    )
                }
            }
            Spacer(Modifier.height(6.dp))

            val totalCells = leadingEmpty + daysInMonth
            val rows = (totalCells + 6) / 7
            var dayCounter = 1
            for (row in 0 until rows) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    for (col in 0 until 7) {
                        val cellIndex = row * 7 + col
                        if (cellIndex < leadingEmpty || dayCounter > daysInMonth) {
                            Box(modifier = Modifier.weight(1f).aspectRatio(1f))
                        } else {
                            val date = yearMonth.atDay(dayCounter)
                            DayCell(
                                date = date,
                                dayOfWeekColumn = col,
                                isToday = date == today,
                                isSelected = date == state.selectedDate,
                                count = state.countsByDate[date],
                                onClick = { onSelect(date) },
                                modifier = Modifier.weight(1f),
                            )
                            dayCounter++
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DayCell(
    date: LocalDate,
    dayOfWeekColumn: Int,
    isToday: Boolean,
    isSelected: Boolean,
    count: DayCount?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val baseColor = when (dayOfWeekColumn) {
        0 -> Color(0xFFE53935)
        6 -> Color(0xFF1E88E5)
        else -> MaterialTheme.colorScheme.onSurface
    }
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .clip(RoundedCornerShape(10.dp))
            .then(
                if (isSelected) {
                    Modifier.background(MaterialTheme.colorScheme.primaryContainer)
                } else {
                    Modifier
                }
            )
            .then(
                if (isToday && !isSelected) {
                    Modifier.border(
                        width = 1.5.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(10.dp),
                    )
                } else {
                    Modifier
                }
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "${date.dayOfMonth}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isToday || isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    baseColor
                },
            )
            if (count != null && count.total > 0) {
                Spacer(Modifier.height(2.dp))
                val allDone = count.done == count.total
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(
                            if (allDone) {
                                MaterialTheme.colorScheme.secondary
                            } else {
                                MaterialTheme.colorScheme.primary
                            }
                        ),
                )
            }
        }
    }
}
