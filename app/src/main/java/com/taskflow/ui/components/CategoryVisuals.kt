package com.taskflow.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Work
import androidx.compose.ui.graphics.vector.ImageVector
import com.taskflow.data.model.Category

/** 카테고리별 대표 아이콘. */
fun Category.icon(): ImageVector = when (this) {
    Category.WORK -> Icons.Filled.Work
    Category.PERSONAL -> Icons.Filled.Person
    Category.STUDY -> Icons.Filled.MenuBook
    Category.HEALTH -> Icons.Filled.FitnessCenter
    Category.OTHER -> Icons.Filled.MoreHoriz
}
