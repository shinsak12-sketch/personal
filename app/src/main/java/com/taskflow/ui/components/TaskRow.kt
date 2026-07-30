package com.taskflow.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.taskflow.data.local.Task
import com.taskflow.ui.util.relativeLabel
import com.taskflow.ui.util.toKorean

/**
 * 할 일 카드 한 줄. 왼쪽 원형 체크로 완료 토글, 카드 탭으로 편집.
 */
@Composable
fun TaskRow(
    task: Task,
    onToggleComplete: (Boolean) -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val categoryColor = Color(task.category.colorArgb)
    val priorityColor = Color(task.priority.colorArgb)
    val done = task.isCompleted

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        shadowElevation = 2.dp,
        onClick = onClick,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // 원형 체크 토글
            Icon(
                imageVector = if (done) Icons.Filled.CheckCircle else Icons.Outlined.Circle,
                contentDescription = if (done) "완료됨" else "미완료",
                tint = if (done) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.outline,
                modifier = Modifier
                    .size(26.dp)
                    .clip(RoundedCornerShape(50))
                    .clickable { onToggleComplete(!done) },
            )
            Spacer(Modifier.width(12.dp))

            // 카테고리 아이콘 칩
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(categoryColor.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = task.category.icon(),
                    contentDescription = null,
                    tint = categoryColor,
                    modifier = Modifier.size(20.dp),
                )
            }
            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = if (done) {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                    textDecoration = if (done) TextDecoration.LineThrough else null,
                )
                Spacer(Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(
                        text = task.category.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = categoryColor,
                        fontWeight = FontWeight.Medium,
                    )
                    task.dueDate?.let { date ->
                        Text("·", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                        Text(
                            text = buildString {
                                append(date.relativeLabel())
                                task.dueTime?.let { append(" ${it.toKorean()}") }
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    if (task.reminderEnabled) {
                        Icon(
                            imageVector = Icons.Filled.NotificationsActive,
                            contentDescription = "알림 설정됨",
                            modifier = Modifier.size(12.dp),
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            }

            Spacer(Modifier.width(8.dp))
            // 우선순위 점
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(RoundedCornerShape(50))
                    .background(priorityColor),
            )
        }
    }
}
