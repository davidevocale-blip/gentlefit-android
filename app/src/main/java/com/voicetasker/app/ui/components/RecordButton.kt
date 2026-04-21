package com.voicetasker.app.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Animated floating action button for voice recording.
 * Shows a pulsing animation when recording is active.
 */
@Composable
fun RecordButton(
    isRecording: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isRecording) 1.3f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = if (isRecording) 0f else 0.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        // Pulse ring effect when recording
        if (isRecording) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .scale(pulseScale)
                    .background(
                        color = MaterialTheme.colorScheme.error.copy(alpha = pulseAlpha),
                        shape = CircleShape
                    )
            )
        }

        FloatingActionButton(
            onClick = onClick,
            containerColor = if (isRecording) {
                MaterialTheme.colorScheme.error
            } else {
                MaterialTheme.colorScheme.primary
            },
            contentColor = Color.White,
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = 8.dp,
                pressedElevation = 12.dp
            ),
            modifier = Modifier.size(64.dp)
        ) {
            Icon(
                imageVector = if (isRecording) Icons.Filled.Stop else Icons.Filled.Mic,
                contentDescription = if (isRecording) "Ferma registrazione" else "Avvia registrazione",
                modifier = Modifier.size(28.dp)
            )
        }
    }
}
