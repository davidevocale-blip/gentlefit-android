package com.voicetasker.app.ui.screen.categories

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.voicetasker.app.ui.theme.CategoryColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen(viewModel: CategoriesViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Categorie", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold) }, colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)) },
        floatingActionButton = { FloatingActionButton(onClick = viewModel::showAddDialog, containerColor = MaterialTheme.colorScheme.primary) { Icon(Icons.Filled.Add, "Aggiungi") } },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(uiState.categories) { cat ->
                val color = try { Color(android.graphics.Color.parseColor(cat.colorHex)) } catch (_: Exception) { MaterialTheme.colorScheme.primary }
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), shape = MaterialTheme.shapes.medium, elevation = CardDefaults.cardElevation(1.dp)) {
                    Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(40.dp).clip(CircleShape).background(color), contentAlignment = Alignment.Center) {
                            Icon(Icons.Filled.Label, null, tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(cat.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                            if (cat.isDefault) Text("Categoria predefinita", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        if (!cat.isDefault) {
                            IconButton(onClick = { viewModel.showEditDialog(cat) }) { Icon(Icons.Filled.Edit, "Modifica", Modifier.size(20.dp)) }
                            IconButton(onClick = { viewModel.deleteCategory(cat.id) }) { Icon(Icons.Filled.Delete, "Elimina", Modifier.size(20.dp), tint = MaterialTheme.colorScheme.error) }
                        }
                    }
                }
            }
        }
    }

    // Add/Edit dialog
    if (uiState.showAddDialog) {
        AlertDialog(
            onDismissRequest = viewModel::dismissDialog,
            title = { Text(if (uiState.editingCategory != null) "Modifica categoria" else "Nuova categoria") },
            text = {
                Column {
                    OutlinedTextField(value = uiState.dialogName, onValueChange = viewModel::onDialogNameChanged, label = { Text("Nome") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium)
                    uiState.errorMessage?.let { Spacer(Modifier.height(4.dp)); Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
                    Spacer(Modifier.height(12.dp))
                    Text("Colore", style = MaterialTheme.typography.labelLarge)
                    Spacer(Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(CategoryColors) { hex ->
                            val c = try { Color(android.graphics.Color.parseColor(hex)) } catch (_: Exception) { MaterialTheme.colorScheme.primary }
                            val sel = uiState.dialogColor == hex
                            Box(Modifier.size(36.dp).clip(CircleShape).background(c).clickable { viewModel.onDialogColorChanged(hex) }, contentAlignment = Alignment.Center) {
                                if (sel) Icon(Icons.Filled.Check, null, tint = Color.White, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = viewModel::saveCategory) { Text("Salva") } },
            dismissButton = { TextButton(onClick = viewModel::dismissDialog) { Text("Annulla") } }
        )
    }
}
