package com.voicetasker.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.voicetasker.app.domain.model.Note
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Card component displaying a voice note summary.
 * Shows title, transcription preview, category, date, and reminder indicator.
 */
@Composable
fun NoteCard(
    note: Note,
    categoryColor: Color,
    categoryName: String,
    hasReminder: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.ITALIAN)

    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Category color indicator
            Box(
                modifier = Modifier
                    .size(4.dp, 48.dp)
                    .clip(CircleShape)
                    .background(categoryColor)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                // Title
                Text(
                    text = note.title.ifBlank { "Nota vocale" },
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Transcription preview
                Text(
                    text = note.transcription.ifBlank { "Nessuna trascrizione" },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Bottom row: category + date + reminder
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CategoryChip(
                        name = categoryName,
                        color = categoryColor,
                        isSmall = true
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (hasReminder) {
                            Icon(
                                imageVector = Icons.Filled.Notifications,
                                contentDescription = "Ha promemoria",
                                modifier = Modifier.size(14.dp),
                                tint = MaterialTheme.colorScheme.tertiary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                        }

                        Icon(
                            imageVector = Icons.Filled.AccessTime,
                            contentDescription = null,
                            modifier = Modifier.size(12.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = dateFormat.format(Date(note.scheduledDate)),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Completed indicator
                if (note.isCompleted) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = "Completato",
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.tertiary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Completato",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                }
            }
        }
    }
}
