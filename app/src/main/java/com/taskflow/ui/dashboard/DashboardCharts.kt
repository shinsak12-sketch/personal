package com.taskflow.ui.dashboard

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.taskflow.ui.util.dayOfWeekShort
import kotlin.math.max

/**
 * 완료율을 보여주는 원형 프로그레스 링.
 */
@Composable
fun ProgressRing(
    progress: Float,
    label: String,
    caption: String,
    modifier: Modifier = Modifier,
    size: androidx.compose.ui.unit.Dp = 120.dp,
) {
    val animated by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(700),
        label = "ring",
    )
    val trackColor = MaterialTheme.colorScheme.surfaceVariant
    val progressColor = MaterialTheme.colorScheme.primary

    Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(size)) {
            val stroke = 14.dp.toPx()
            val inset = stroke / 2
            val arcSize = Size(this.size.width - stroke, this.size.height - stroke)
            drawArc(
                color = trackColor,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(inset, inset),
                size = arcSize,
                style = Stroke(width = stroke, cap = StrokeCap.Round),
            )
            drawArc(
                color = progressColor,
                startAngle = -90f,
                sweepAngle = 360f * animated,
                useCenter = false,
                topLeft = Offset(inset, inset),
                size = arcSize,
                style = Stroke(width = stroke, cap = StrokeCap.Round),
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = label,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = caption,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

/**
 * 최근 7일 완료 현황 막대 그래프.
 */
@Composable
fun WeeklyBarChart(
    data: List<DayProgress>,
    modifier: Modifier = Modifier,
) {
    val maxTotal = max(1, data.maxOfOrNull { it.total } ?: 1)
    val today = data.lastOrNull()?.date
    val barColor = MaterialTheme.colorScheme.primary
    val doneColor = MaterialTheme.colorScheme.secondary
    val trackColor = MaterialTheme.colorScheme.surfaceVariant

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(140.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom,
    ) {
        data.forEach { day ->
            val isToday = day.date == today
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = if (day.total > 0) "${day.done}/${day.total}" else "",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .width(18.dp)
                        .height(90.dp),
                    contentAlignment = Alignment.BottomCenter,
                ) {
                    // 전체 대비 트랙
                    Box(
                        modifier = Modifier
                            .width(18.dp)
                            .fillMaxHeight(day.total.toFloat() / maxTotal)
                            .clip(RoundedCornerShape(6.dp))
                            .background(trackColor),
                    )
                    // 완료 비율
                    val doneFraction = if (maxTotal == 0) 0f else day.done.toFloat() / maxTotal
                    Box(
                        modifier = Modifier
                            .width(18.dp)
                            .fillMaxHeight(doneFraction)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isToday) barColor else doneColor),
                    )
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    text = day.date.dayOfWeekShort(),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                    color = if (isToday) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                )
            }
        }
    }
}

/**
 * 카테고리별 활성 업무 분포(가로 막대).
 */
@Composable
fun CategoryBars(
    slices: List<CategorySlice>,
    modifier: Modifier = Modifier,
) {
    val total = max(1, slices.sumOf { it.count })
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        slices.forEach { slice ->
            val color = Color(slice.category.colorArgb)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(color),
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = slice.category.label,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.width(44.dp),
                )
                Spacer(Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(slice.count.toFloat() / total)
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .background(color),
                    )
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "${slice.count}",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(start = 2.dp),
                )
            }
        }
    }
}
