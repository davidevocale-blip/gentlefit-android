package com.voicetasker.app.ui.screen.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.voicetasker.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: SettingsViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Impostazioni", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold) }, colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp)) {
            // Premium section
            Card(colors = CardDefaults.cardColors(containerColor = Color.Transparent), shape = MaterialTheme.shapes.large) {
                Box(Modifier.fillMaxWidth().heightIn(min = 180.dp).padding(24.dp)) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Star, null, tint = Gold40, modifier = Modifier.size(28.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("VoiceTasker Premium", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        }
                        Spacer(Modifier.height(12.dp))
                        val features = listOf("Note vocali illimitate", "Registrazioni fino a 10 min", "Categorie personalizzabili illimitate", "Reminder multipli e personalizzati", "Nessuna pubblicità", "Esportazione PDF / CSV", "Temi premium")
                        features.forEach { feature -> Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 2.dp)) { Icon(Icons.Filled.Check, null, tint = Mint40, modifier = Modifier.size(16.dp)); Spacer(Modifier.width(8.dp)); Text(feature, style = MaterialTheme.typography.bodyMedium) } }
                        Spacer(Modifier.height(16.dp))

                        // Pricing buttons
                        Button(onClick = viewModel::onPurchasePremium, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Purple40), shape = MaterialTheme.shapes.medium) {
                            Text("Mensile — €3,99/mese", fontWeight = FontWeight.SemiBold)
                        }
                        Spacer(Modifier.height(8.dp))
                        OutlinedButton(onClick = viewModel::onPurchasePremium, modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium) {
                            Text("Annuale — €29,99/anno (risparmia 37%)")
                        }
                        Spacer(Modifier.height(8.dp))
                        TextButton(onClick = viewModel::onPurchasePremium, modifier = Modifier.fillMaxWidth()) {
                            Text("Lifetime — €49,99 una tantum")
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            Spacer(Modifier.height(16.dp))

            // Theme
            Text("Tema", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))
            listOf("system" to "Automatico", "light" to "Chiaro", "dark" to "Scuro").forEach { (value, label) ->
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    RadioButton(selected = uiState.themeMode == value, onClick = { viewModel.onThemeChanged(value) })
                    Spacer(Modifier.width(8.dp))
                    Text(label, style = MaterialTheme.typography.bodyLarge)
                }
            }

            Spacer(Modifier.height(24.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            Spacer(Modifier.height(16.dp))

            // Info
            Text("Informazioni", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))
            Text("VoiceTasker v1.0.0", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("Sviluppato con ❤️ in Italia", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(32.dp))
        }
    }
}
