package com.gentlefit.app.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gentlefit.app.ui.theme.Plum40
import com.gentlefit.app.ui.theme.Plum80
import com.gentlefit.app.ui.theme.SuccessGreen
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun AnimatedCounter(targetValue: Int, label: String, emoji: String, modifier: Modifier = Modifier) {
    var displayValue by remember { mutableIntStateOf(0) }
    LaunchedEffect(targetValue) {
        if (targetValue > 0) {
            val step = (targetValue / 20).coerceAtLeast(1)
            var current = 0
            while (current < targetValue) {
                current = (current + step).coerceAtMost(targetValue)
                displayValue = current
                delay(30)
            }
        }
    }
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(emoji, fontSize = 20.sp)
        Text("$displayValue", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun WeeklyRing(progress: Float, size: Dp = 100.dp, modifier: Modifier = Modifier) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 1200, easing = FastOutSlowInEasing), label = "ring"
    )
    Box(modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val stroke = 10.dp.toPx()
            drawArc(Plum80, 0f, 360f, false, style = Stroke(stroke, cap = StrokeCap.Round))
            drawArc(Plum40, -90f, animatedProgress * 360f, false, style = Stroke(stroke, cap = StrokeCap.Round))
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("${(animatedProgress * 100).toInt()}%", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Plum40)
            Text("settimana", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 9.sp)
        }
    }
}

@Composable
fun StaggeredAnimatedColumn(itemCount: Int, content: @Composable (index: Int) -> Unit) {
    Column {
        repeat(itemCount) { index ->
            var visible by remember { mutableStateOf(false) }
            LaunchedEffect(Unit) { delay(index * 80L); visible = true }
            AnimatedVisibility(
                visible = visible,
                enter = slideInVertically(initialOffsetY = { 60 }) + fadeIn(tween(300))
            ) { content(index) }
        }
    }
}

@Composable
fun CelebrationBurst(show: Boolean, modifier: Modifier = Modifier) {
    if (!show) return
    val particles = remember { List(12) { Random.nextFloat() to Random.nextFloat() } }
    val anim = rememberInfiniteTransition(label = "burst")
    val progress by anim.animateFloat(0f, 1f, infiniteRepeatable(tween(1500), RepeatMode.Restart), label = "p")

    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        particles.forEach { (angle, speed) ->
            val rad = angle * 2 * PI.toFloat()
            val dist = progress * (60 + speed * 40)
            val x = cos(rad) * dist
            val y = sin(rad) * dist
            Box(
                Modifier.offset(x.dp, y.dp).size((6 - progress * 4).dp)
                    .alpha(1f - progress).clip(CircleShape)
                    .background(listOf(Plum40, SuccessGreen, Color(0xFFFFCA28), Plum80).random())
            )
        }
    }
}

@Composable
fun PulseButton(content: @Composable () -> Unit) {
    val scale by rememberInfiniteTransition(label = "pulse").animateFloat(
        1f, 1.05f, infiniteRepeatable(tween(800), RepeatMode.Reverse), label = "s"
    )
    Box(Modifier.scale(scale)) { content() }
}
