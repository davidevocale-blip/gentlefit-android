package com.voicetasker.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.voicetasker.app.ui.theme.Gold40
import com.voicetasker.app.ui.theme.Purple40
import com.voicetasker.app.ui.theme.Purple60

/**
 * Banner prompting free users to upgrade to Premium.
 * Features a gradient background and star icon.
 */
@Composable
fun PremiumBanner(
    notesRemaining: Int,
    onUpgradeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(Purple40, Purple60)
                ),
                shape = MaterialTheme.shapes.medium
            )
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                tint = Gold40,
                modifier = Modifier
                    .padding(end = 12.dp)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Passa a Premium",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = if (notesRemaining > 0) {
                        "$notesRemaining note gratuite rimanenti questo mese"
                    } else {
                        "Hai esaurito le note gratuite del mese"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = onUpgradeClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Gold40,
                    contentColor = Purple40
                ),
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    text = "Upgrade",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
