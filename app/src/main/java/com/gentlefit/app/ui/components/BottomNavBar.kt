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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gentlefit.app.ui.theme.Plum40
import com.gentlefit.app.ui.theme.Plum80

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
    Box(modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp)) {
        NavigationBar(
            modifier = Modifier.clip(RoundedCornerShape(20.dp)).height(64.dp),
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp
        ) {
            bottomNavItems.forEach { item ->
                val selected = currentRoute == item.route
                NavigationBarItem(
                    selected = selected,
                    onClick = { onNavigate(item.route) },
                    icon = {
                        if (selected) {
                            Box(Modifier.size(36.dp).clip(CircleShape).background(Brush.linearGradient(listOf(Plum40, Plum80))), contentAlignment = Alignment.Center) {
                                Icon(item.icon, item.label, Modifier.size(18.dp), tint = Color.White)
                            }
                        } else {
                            Icon(item.icon, item.label, Modifier.size(20.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    },
                    label = {
                        Text(item.label, fontSize = 9.sp, fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
                    },
                    colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent)
                )
            }
        }
    }
}
