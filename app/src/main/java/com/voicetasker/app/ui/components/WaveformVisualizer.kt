package com.voicetasker.app.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

/**
 * Animated audio waveform visualizer.
 * Shows dynamic bars that react to audio amplitude during recording.
 */
@Composable
fun WaveformVisualizer(
    amplitudes: List<Int>,
    isActive: Boolean,
    modifier: Modifier = Modifier,
    barColor: Color = MaterialTheme.colorScheme.primary,
    barCount: Int = 40
) {
    val animatedAmplitudes = remember { List(barCount) { Animatable(0.1f) } }

    LaunchedEffect(amplitudes, isActive) {
        if (isActive && amplitudes.isNotEmpty()) {
            amplitudes.takeLast(barCount).forEachIndexed { index, amplitude ->
                val normalizedAmp = (amplitude / 32767f).coerceIn(0.05f, 1f)
                if (index < animatedAmplitudes.size) {
                    animatedAmplitudes[index].animateTo(
                        targetValue = normalizedAmp,
                        animationSpec = tween(durationMillis = 100)
                    )
                }
            }
        } else if (!isActive) {
            animatedAmplitudes.forEach { anim ->
                anim.animateTo(0.1f, animationSpec = tween(300))
            }
        }
    }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val barWidth = (canvasWidth / barCount) * 0.6f
        val spacing = (canvasWidth / barCount) * 0.4f
        val cornerRadius = CornerRadius(barWidth / 2, barWidth / 2)

        animatedAmplitudes.forEachIndexed { index, anim ->
            val barHeight = (anim.value * canvasHeight).coerceIn(4f, canvasHeight)
            val x = index * (barWidth + spacing) + spacing / 2
            val y = (canvasHeight - barHeight) / 2

            drawRoundRect(
                color = barColor.copy(alpha = 0.3f + anim.value * 0.7f),
                topLeft = Offset(x, y),
                size = Size(barWidth, barHeight),
                cornerRadius = cornerRadius
            )
        }
    }
}
