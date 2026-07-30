package com.taskflow.ui.calendar

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import com.taskflow.ui.components.GradientFab
import com.taskflow.ui.components.SectionHeader
import com.taskflow.ui.components.TaskRow
import com.taskflow.ui.theme.OnBrand
import com.taskflow.ui.theme.OnBrandMuted
import com.taskflow.ui.theme.brandBrush
import com.taskflow.ui.util.toKorean
import com.taskflow.ui.util.toMonthTitle
import java.time.LocalDate

private val weekdayLabels = listOf("일", "월", "화", "수", "목", "금", "토")

@Composable
fun CalendarScreen(
    onAddTask: (LocalDate) -> Unit,
    onOpenTask: (Long) -> Unit,
    viewModel: CalendarViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            GradientFab(
                onClick = { onAddTask(state.selectedDate) },
                icon = Icons.Filled.Add,
                contentDescription = "일정 추가",
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                HeroHeader(
                    monthTitle = state.yearMonth.atDay(1).toMonthTitle(),
                    monthDone = state.monthDone,
                    monthTotal = state.monthTotal,
                    onPrev = viewModel::previousMonth,
                    onNext = viewModel::nextMonth,
                    onToday = viewModel::goToToday,
                )
            }
            item {
                MonthGrid(state = state, onSelect = viewModel::selectDate)
            }
            item {
                SectionHeader(
                    title = "${state.selectedDate.toKorean()}",
                    action = {
                        if (state.selectedTasks.isNotEmpty()) {
                            Text(
                                text = "완료 ${state.selectedDone}/${state.selectedTasks.size}",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    },
                )
            }
            if (state.selectedTasks.isEmpty()) {
                item { EmptyState(emoji = "🗓️", message = "이 날은 등록된 일정이 없습니다.\n+ 버튼으로 추가해 보세요.") }
            } else {
                items(state.selectedTasks, key = { it.id }) { task ->
                    TaskRow(
                        task = task,
                        onToggleComplete = { checked -> viewModel.toggleComplete(task, checked) },
                        onClick = { onOpenTask(task.id) },
                    )
                }
            }
            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}

@Composable
private fun HeroHeader(
    monthTitle: String,
    monthDone: Int,
    monthTotal: Int,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    onToday: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(brandBrush())
            .padding(20.dp),
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = monthTitle,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = OnBrand,
                    )
                    Text(
                        text = if (monthTotal == 0) "이번 달 일정이 없어요" else "이번 달 $monthDone / $monthTotal 완료",
                        style = MaterialTheme.typography.bodyMedium,
                        color = OnBrandMuted,
                    )
                }
                CircleIconButton(icon = Icons.Filled.ChevronLeft, description = "이전 달", onClick = onPrev)
                Spacer(Modifier.size(8.dp))
                CircleIconButton(icon = Icons.Filled.ChevronRight, description = "다음 달", onClick = onNext)
            }
            Spacer(Modifier.height(14.dp))
            // 진행 바
            val progress = if (monthTotal == 0) 0f else monthDone.toFloat() / monthTotal
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color.White.copy(alpha = 0.25f)),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .height(8.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Color.White),
                )
            }
            Spacer(Modifier.height(10.dp))
            Text(
                text = "오늘로 이동",
                style = MaterialTheme.typography.labelLarge,
                color = OnBrand,
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(Color.White.copy(alpha = 0.18f))
                    .clickable(onClick = onToday)
                    .padding(horizontal = 14.dp, vertical = 6.dp),
            )
        }
    }
}

@Composable
private fun CircleIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    description: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.18f))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription = description, tint = OnBrand)
    }
}

@Composable
private fun MonthGrid(
    state: CalendarUiState,
    onSelect: (LocalDate) -> Unit,
) {
    val yearMonth = state.yearMonth
    val firstDay = yearMonth.atDay(1)
    val leadingEmpty = firstDay.dayOfWeek.value % 7 // 일요일 시작
    val daysInMonth = yearMonth.lengthOfMonth()
    val today = LocalDate.now()

    Surface(
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                weekdayLabels.forEachIndexed { index, label ->
                    Text(
                        text = label,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = when (index) {
                            0 -> Color(0xFFF06A6A)
                            6 -> Color(0xFF4C8DF5)
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        },
                    )
                }
            }
            Spacer(Modifier.height(8.dp))

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
                                column = col,
                                isToday = date == today,
                                isSelected = date == state.selectedDate,
                                info = state.countsByDate[date],
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
    column: Int,
    isToday: Boolean,
    isSelected: Boolean,
    info: DayInfo?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val weekendColor = when (column) {
        0 -> Color(0xFFF06A6A)
        6 -> Color(0xFF4C8DF5)
        else -> MaterialTheme.colorScheme.onSurface
    }
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(3.dp)
            .clip(RoundedCornerShape(14.dp))
            .then(
                when {
                    isSelected -> Modifier.background(brandBrush())
                    isToday -> Modifier.background(MaterialTheme.colorScheme.primaryContainer)
                    else -> Modifier
                }
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "${date.dayOfMonth}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isToday || isSelected) FontWeight.Bold else FontWeight.Normal,
                color = when {
                    isSelected -> OnBrand
                    isToday -> MaterialTheme.colorScheme.onPrimaryContainer
                    else -> weekendColor
                },
            )
            Spacer(Modifier.height(3.dp))
            // 카테고리 색 점들 (선택 시엔 흰 점)
            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                val colors = info?.categoryColors ?: emptyList()
                colors.forEach { argb ->
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) Color.White else Color(argb)),
                    )
                }
                // 점이 없더라도 높이 유지
                if (colors.isEmpty()) {
                    Box(modifier = Modifier.size(5.dp))
                }
            }
        }
    }
}
