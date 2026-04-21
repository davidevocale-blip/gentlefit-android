package com.voicetasker.app.ui.components

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Custom monthly calendar view.
 * Shows days in a grid with indicators for days that have notes.
 */
@Composable
fun CalendarView(
    selectedDate: Long,
    currentMonth: Calendar,
    daysWithNotes: Set<Int>,
    onDateSelected: (Long) -> Unit,
    onMonthChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val monthFormat = SimpleDateFormat("MMMM yyyy", Locale.ITALIAN)
    val today = Calendar.getInstance()

    val selectedCal = Calendar.getInstance().apply { timeInMillis = selectedDate }
    val selectedDay = selectedCal.get(Calendar.DAY_OF_MONTH)
    val selectedMonth = selectedCal.get(Calendar.MONTH)
    val selectedYear = selectedCal.get(Calendar.YEAR)

    val calMonth = currentMonth.get(Calendar.MONTH)
    val calYear = currentMonth.get(Calendar.YEAR)

    val daysInMonth = currentMonth.getActualMaximum(Calendar.DAY_OF_MONTH)
    val firstDayOfWeek = Calendar.getInstance().apply {
        set(calYear, calMonth, 1)
    }.get(Calendar.DAY_OF_WEEK)

    // Adjust to Monday-first (European standard)
    val startOffset = (firstDayOfWeek + 5) % 7

    val dayNames = listOf("Lun", "Mar", "Mer", "Gio", "Ven", "Sab", "Dom")

    Column(modifier = modifier.fillMaxWidth()) {
        // Month navigation
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { onMonthChanged(-1) }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Mese precedente",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            Text(
                text = monthFormat.format(currentMonth.time).replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            IconButton(onClick = { onMonthChanged(1) }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Mese successivo",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Day headers
        Row(modifier = Modifier.fillMaxWidth()) {
            dayNames.forEach { dayName ->
                Text(
                    text = dayName,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Day grid
        var dayCounter = 1
        val totalCells = startOffset + daysInMonth
        val rows = (totalCells + 6) / 7

        for (row in 0 until rows) {
            Row(modifier = Modifier.fillMaxWidth()) {
                for (col in 0 until 7) {
                    val cellIndex = row * 7 + col
                    if (cellIndex < startOffset || dayCounter > daysInMonth) {
                        Box(modifier = Modifier.weight(1f).height(40.dp))
                    } else {
                        val day = dayCounter
                        val isToday = day == today.get(Calendar.DAY_OF_MONTH) &&
                                calMonth == today.get(Calendar.MONTH) &&
                                calYear == today.get(Calendar.YEAR)
                        val isSelected = day == selectedDay &&
                                calMonth == selectedMonth &&
                                calYear == selectedYear
                        val hasNote = day in daysWithNotes

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .clip(CircleShape)
                                .then(
                                    if (isSelected) {
                                        Modifier.background(
                                            MaterialTheme.colorScheme.primary,
                                            CircleShape
                                        )
                                    } else if (isToday) {
                                        Modifier.background(
                                            MaterialTheme.colorScheme.primaryContainer,
                                            CircleShape
                                        )
                                    } else {
                                        Modifier
                                    }
                                )
                                .clickable {
                                    val cal = Calendar.getInstance().apply {
                                        set(calYear, calMonth, day, 0, 0, 0)
                                        set(Calendar.MILLISECOND, 0)
                                    }
                                    onDateSelected(cal.timeInMillis)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = day.toString(),
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = if (isToday || isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = when {
                                        isSelected -> MaterialTheme.colorScheme.onPrimary
                                        isToday -> MaterialTheme.colorScheme.primary
                                        else -> MaterialTheme.colorScheme.onSurface
                                    }
                                )
                                if (hasNote) {
                                    Box(
                                        modifier = Modifier
                                            .size(4.dp)
                                            .background(
                                                if (isSelected) MaterialTheme.colorScheme.onPrimary
                                                else MaterialTheme.colorScheme.primary,
                                                CircleShape
                                            )
                                    )
                                }
                            }
                        }
                        dayCounter++
                    }
                }
            }
        }
    }
}
