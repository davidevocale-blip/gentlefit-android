package com.gentlefit.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ShowChart
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gentlefit.app.ui.theme.Plum30
import com.gentlefit.app.ui.theme.Plum40
import com.gentlefit.app.ui.theme.Plum70

data class BottomNavItem(val route: String, val label: String, val icon: ImageVector)

val bottomNavItems = listOf(
    BottomNavItem("home", "Home", Icons.Rounded.Home),
    BottomNavItem("coach", "Coach", Icons.Rounded.EmojiEmotions),
    BottomNavItem("progress", "Progressi", Icons.AutoMirrored.Rounded.ShowChart),
    BottomNavItem("goals", "Obiettivi", Icons.Rounded.FitnessCenter),
    BottomNavItem("news", "News", Icons.Rounded.Newspaper),
)

@Composable
fun GentleFitBottomNav(currentRoute: String, onNavigate: (String) -> Unit, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
            .navigationBarsPadding(),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp,
        tonalElevation = 4.dp
    ) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            bottomNavItems.forEach { item ->
                val selected = currentRoute == item.route
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (selected) Plum40.copy(0.12f) else Color.Transparent)
                        .padding(vertical = 6.dp)
                ) {
                    IconButton(
                        onClick = { onNavigate(item.route) },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            item.icon, item.label,
                            modifier = Modifier.size(22.dp),
                            tint = if (selected) Plum30 else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        item.label,
                        fontSize = 10.sp,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                        color = if (selected) Plum30 else MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
            }
        }
    }
}
