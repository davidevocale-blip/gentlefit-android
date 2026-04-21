package com.voicetasker.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Chip component for displaying a category with its color.
 */
@Composable
fun CategoryChip(
    name: String,
    color: Color,
    isSmall: Boolean = false,
    isSelected: Boolean = false,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val shape = if (isSmall) MaterialTheme.shapes.small else MaterialTheme.shapes.medium

    Surface(
        onClick = { onClick?.invoke() },
        enabled = onClick != null,
        shape = shape,
        color = if (isSelected) {
            color.copy(alpha = 0.2f)
        } else {
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        },
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(
                horizontal = if (isSmall) 8.dp else 12.dp,
                vertical = if (isSmall) 4.dp else 6.dp
            )
        ) {
            Box(
                modifier = Modifier
                    .size(if (isSmall) 8.dp else 10.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(modifier = Modifier.width(if (isSmall) 4.dp else 6.dp))
            Text(
                text = name,
                style = if (isSmall) {
                    MaterialTheme.typography.labelSmall
                } else {
                    MaterialTheme.typography.labelMedium
                },
                color = if (isSelected) color else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
