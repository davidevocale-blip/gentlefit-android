package com.voicetasker.app.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.voicetasker.app.domain.model.ReminderType

/**
 * Component for selecting reminder types.
 * Shows checkboxes for 1 day, 12 hours, and 2 hours before.
 */
@Composable
fun ReminderPicker(
    selectedTypes: Set<ReminderType>,
    onTypeToggled: (ReminderType) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Column(modifier = modifier) {
        Text(
            text = "Promemoria",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))

        ReminderType.entries.forEach { type ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 2.dp)
            ) {
                Checkbox(
                    checked = type in selectedTypes,
                    onCheckedChange = { onTypeToggled(type) },
                    enabled = enabled,
                    colors = CheckboxDefaults.colors(
                        checkedColor = MaterialTheme.colorScheme.primary,
                        uncheckedColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
                Text(
                    text = type.label,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (enabled) {
                        MaterialTheme.colorScheme.onSurface
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
        }
    }
}
