package com.gentlefit.app.ui.screen.profile

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.gentlefit.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    onNavigateToPremium: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var weightInput by remember { mutableStateOf("") }
    val photoLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        uri?.let { viewModel.updatePhotoUri(it.toString()) }
    }

    Column(
        Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState()).statusBarsPadding()
    ) {
        // Header
        Box(
            Modifier.fillMaxWidth()
                .background(Brush.verticalGradient(listOf(Plum80.copy(0.5f), MaterialTheme.colorScheme.background)))
                .padding(20.dp)
        ) {
            Column {
                IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Rounded.ArrowBack, "Indietro") }
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier.size(72.dp).clip(CircleShape).background(Plum60.copy(0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (state.photoUri.isNotBlank()) {
                            AsyncImage(state.photoUri, "Foto profilo", Modifier.fillMaxSize().clip(CircleShape), contentScale = ContentScale.Crop)
                        } else {
                            Text("🌸", fontSize = 32.sp)
                        }
                    }
                    Spacer(Modifier.width(16.dp))
                    Column(Modifier.weight(1f)) {
                        Text(state.userName.ifBlank { "Utente" }, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                        if (state.userGoal.isNotBlank()) Text(state.userGoal, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    IconButton(onClick = { viewModel.toggleEditProfile() }) {
                        Icon(Icons.Rounded.Edit, "Modifica", tint = Plum40)
                    }
                }
            }
        }

        // Edit Profile Section
        AnimatedVisibility(
            visible = state.isEditingProfile,
            enter = expandVertically(tween(300)) + fadeIn(),
            exit = shrinkVertically(tween(200)) + fadeOut()
        ) {
            Card(
                Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Plum90)
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("✏️ Modifica Profilo", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)

                    OutlinedButton(onClick = { photoLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                        Icon(Icons.Rounded.CameraAlt, null, Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Cambia foto")
                    }

                    var editName by remember(state.userName) { mutableStateOf(state.userName) }
                    OutlinedTextField(editName, { editName = it; viewModel.updateName(it) }, label = { Text("Nome") },
                        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), singleLine = true)

                    var editHeight by remember(state.height) { mutableStateOf(if (state.height > 0) state.height.toString() else "") }
                    OutlinedTextField(editHeight, { editHeight = it; it.toFloatOrNull()?.let { h -> viewModel.updateHeight(h) } },
                        label = { Text("Altezza (cm)") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                        singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))

                    val bodyTypes = listOf("Esile", "Normale", "Robusta", "Curvy")
                    var expanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                        OutlinedTextField(state.bodyType, {}, readOnly = true, label = { Text("Corporatura") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                            modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable, true), shape = RoundedCornerShape(12.dp))
                        ExposedDropdownMenu(expanded, { expanded = false }) {
                            bodyTypes.forEach { type ->
                                DropdownMenuItem(text = { Text(type) }, onClick = { viewModel.updateBodyType(type); expanded = false })
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // Stats
        Row(Modifier.fillMaxWidth().padding(horizontal = 24.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
            StatCard("🔥", "${state.streakDays}", "Streak")
            StatCard("✅", "${state.completedDays}", "Completati")
        }

        Spacer(Modifier.height(16.dp))

        // Bilancia Amica
        Card(
            Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = if (state.canRecordWeight) SageGreen90 else Mauve90)
        ) {
            Column(Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("⚖️", fontSize = 28.sp)
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Bilancia Amica", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Text("Pesati solo 1 volta a settimana", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Spacer(Modifier.height(12.dp))
                if (state.currentWeight > 0) {
                    Text("Peso attuale: ${String.format("%.1f", state.currentWeight)} kg", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                    Spacer(Modifier.height(4.dp))
                }
                LinearProgressIndicator(
                    progress = { (state.weeklyUsageCount / 4f).coerceAtMost(1f) },
                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                    color = if (state.canRecordWeight) SuccessGreen else Plum60,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
                Spacer(Modifier.height(8.dp))

                if (state.canRecordWeight) {
                    OutlinedTextField(weightInput, { weightInput = it }, label = { Text("Peso (kg)") },
                        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = { weightInput.replace(",", ".").toFloatOrNull()?.let { viewModel.recordWeight(it) } },
                        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Plum40)) {
                        Text("📝 Registra peso")
                    }
                } else {
                    Text(state.weightBlockReason, style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 18.sp)
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Settings
        Column(Modifier.padding(horizontal = 24.dp)) {
            Text("Impostazioni", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))
            SettingsToggle("Mostra peso", "Traccia il peso nei progressi", state.showWeight) { viewModel.toggleWeight(it) }
            SettingsToggle("Notifiche", "Promemoria giornalieri", state.notifications) { viewModel.toggleNotifications(it) }

            Spacer(Modifier.height(16.dp))

            // Invite friend
            Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SageGreen80.copy(0.4f))) {
                Row(Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text("👯", fontSize = 28.sp)
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Invita un'amica", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                        Text("Motivatevi a vicenda!", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    TextButton(onClick = {
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, "Prova GentleFit! L'app per il benessere senza sforzo 🌸 Insieme è più facile! 💕")
                        }
                        context.startActivity(Intent.createChooser(intent, "Invita un'amica"))
                    }) { Text("Invita", color = SageGreen40, fontWeight = FontWeight.SemiBold) }
                }
            }

            Spacer(Modifier.height(12.dp))

            // Premium CTA
            Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Lavender80.copy(0.5f))) {
                Row(Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text("💎", fontSize = 28.sp)
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Passa a Premium", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                        Text("Sblocca tutti i contenuti", style = MaterialTheme.typography.bodySmall)
                    }
                    TextButton(onClick = onNavigateToPremium) { Text("Scopri", color = Lavender40, fontWeight = FontWeight.SemiBold) }
                }
            }
        }
        Spacer(Modifier.height(40.dp))
    }
}

@Composable
private fun StatCard(emoji: String, value: String, label: String) {
    Card(Modifier.width(140.dp), shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)) {
        Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(emoji, fontSize = 24.sp)
            Text(value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun SettingsToggle(title: String, subtitle: String, checked: Boolean, onToggle: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(checked = checked, onCheckedChange = onToggle, colors = SwitchDefaults.colors(checkedTrackColor = Plum40))
    }
}
