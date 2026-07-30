package com.taskflow.ui.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.taskflow.ui.AppViewModelProvider
import com.taskflow.ui.components.EmptyState
import com.taskflow.ui.components.SectionHeader
import com.taskflow.ui.components.TaskRow
import com.taskflow.ui.util.toKorean
import kotlin.math.roundToInt

@Composable
fun DashboardScreen(
    onAddTask: () -> Unit,
    onOpenTask: (Long) -> Unit,
    onSeeAllTasks: () -> Unit,
    viewModel: DashboardViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddTask,
                icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                text = { Text("새 일정") },
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
            item { Header(state) }
            item { TodaySummaryCard(state) }
            item { WeeklyCard(state) }
            if (state.categories.isNotEmpty()) {
                item { CategoryCard(state) }
            }
            if (state.overdue.isNotEmpty()) {
                item { OverdueCard(count = state.overdue.size) }
                items(state.overdue.take(3), key = { "overdue_${it.id}" }) { task ->
                    TaskRow(task = task, onToggleComplete = {}, onClick = { onOpenTask(task.id) })
                }
            }
            item {
                SectionHeader(
                    title = "오늘 일정",
                    action = {
                        TextButton(onClick = onSeeAllTasks) { Text("전체 보기") }
                    },
                )
            }
            if (state.todayTasks.isEmpty()) {
                item { EmptyState(emoji = "☀️", message = "오늘 예정된 일정이 없습니다.") }
            } else {
                items(state.todayTasks, key = { it.id }) { task ->
                    TaskRow(task = task, onToggleComplete = {}, onClick = { onOpenTask(task.id) })
                }
            }
            item { Spacer(Modifier.height(72.dp)) }
        }
    }
}

@Composable
private fun Header(state: DashboardUiState) {
    Column {
        Text(
            text = greetingFor(),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = "${state.today.toKorean()} · 진행 중 ${state.activeTotal}건",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun TodaySummaryCard(state: DashboardUiState) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            ProgressRing(
                progress = state.todayRate,
                label = "${(state.todayRate * 100).roundToInt()}%",
                caption = "오늘 완료율",
            )
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                StatLine(value = "${state.todayDone}", label = "오늘 완료")
                StatLine(value = "${(state.todayTotal - state.todayDone).coerceAtLeast(0)}", label = "오늘 남음")
                StatLine(value = "${state.completedTotal}", label = "누적 완료")
            }
        }
    }
}

@Composable
private fun StatLine(value: String, label: String) {
    Row(verticalAlignment = Alignment.Bottom) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
        )
        Spacer(Modifier.width(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun WeeklyCard(state: DashboardUiState) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "최근 7일 완료 현황",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(16.dp))
            WeeklyBarChart(data = state.weekly)
        }
    }
}

@Composable
private fun CategoryCard(state: DashboardUiState) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "카테고리별 진행 중",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(16.dp))
            CategoryBars(slices = state.categories)
        }
    }
}

@Composable
private fun OverdueCard(count: Int) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Filled.WarningAmber,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onErrorContainer,
            )
            Spacer(Modifier.width(12.dp))
            Text(
                text = "기한이 지난 할 일이 ${count}건 있습니다.",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onErrorContainer,
            )
        }
    }
}

private fun greetingFor(): String {
    val hour = java.time.LocalTime.now().hour
    return when (hour) {
        in 5..10 -> "좋은 아침이에요 👋"
        in 11..13 -> "점심 잘 챙기세요 🍚"
        in 14..17 -> "오후도 화이팅! 💪"
        in 18..21 -> "오늘 하루 수고했어요 🌙"
        else -> "편안한 밤 되세요 😴"
    }
}
