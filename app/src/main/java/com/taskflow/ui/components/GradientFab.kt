package com.taskflow.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.taskflow.ui.theme.BrandStart
import com.taskflow.ui.theme.brandBrush

/** 브랜드 그라데이션이 적용된 원형 FAB. */
@Composable
fun GradientFab(
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(60.dp)
            .shadow(12.dp, RoundedCornerShape(20.dp), spotColor = BrandStart)
            .clip(RoundedCornerShape(20.dp))
            .background(brandBrush())
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = Color.White,
            modifier = Modifier.size(26.dp),
        )
    }
}
