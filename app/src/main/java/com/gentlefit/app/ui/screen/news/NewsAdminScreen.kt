package com.gentlefit.app.ui.screen.news

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gentlefit.app.domain.model.NewsArticle
import com.gentlefit.app.domain.model.NewsCategory
import com.gentlefit.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsAdminScreen(onBack: () -> Unit, viewModel: NewsAdminViewModel = hiltViewModel()) {
    val articles by viewModel.allNews.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }
    var editingArticle by remember { mutableStateOf<NewsArticle?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("📰 Gestione News", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Rounded.ArrowBack, "Indietro") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showCreateDialog = true }, containerColor = Plum40, contentColor = androidx.compose.ui.graphics.Color.White) {
                Icon(Icons.Rounded.Add, "Nuovo articolo")
            }
        }
    ) { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding).padding(horizontal = 24.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            item { Spacer(Modifier.height(8.dp)) }
            items(articles, key = { it.id }) { article ->
                AdminNewsCard(article, onEdit = { editingArticle = article }, onDelete = { viewModel.deleteArticle(article.id) })
            }
            item { Spacer(Modifier.height(80.dp)) }
        }
    }

    if (showCreateDialog) {
        NewsEditDialog(article = null, onDismiss = { showCreateDialog = false }, onSave = { t, s, c, cat ->
            viewModel.createArticle(t, s, c, cat); showCreateDialog = false
        })
    }

    editingArticle?.let { art ->
        NewsEditDialog(article = art, onDismiss = { editingArticle = null }, onSave = { t, s, c, cat ->
            viewModel.updateArticle(art.copy(title = t, summary = s, content = c, category = cat)); editingArticle = null
        })
    }
}

@Composable
private fun AdminNewsCard(article: NewsArticle, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("${article.category.emoji} ${article.category.displayName}", style = MaterialTheme.typography.labelSmall, color = Plum40)
                Text(article.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, maxLines = 2)
                Text(article.publishedDate, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            IconButton(onClick = onEdit) { Icon(Icons.Rounded.Edit, "Modifica", tint = Plum40) }
            IconButton(onClick = onDelete) { Icon(Icons.Rounded.Delete, "Elimina", tint = ErrorSoft) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NewsEditDialog(article: NewsArticle?, onDismiss: () -> Unit, onSave: (String, String, String, NewsCategory) -> Unit) {
    var title by remember { mutableStateOf(article?.title ?: "") }
    var summary by remember { mutableStateOf(article?.summary ?: "") }
    var content by remember { mutableStateOf(article?.content ?: "") }
    var category by remember { mutableStateOf(article?.category ?: NewsCategory.BENESSERE) }
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (article == null) "Nuovo Articolo" else "Modifica Articolo", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(title, { title = it }, label = { Text("Titolo") }, modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp), singleLine = true)
                OutlinedTextField(summary, { summary = it }, label = { Text("Sommario") }, modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp), maxLines = 3)
                OutlinedTextField(content, { content = it }, label = { Text("Contenuto") }, modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp), maxLines = 6)
                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                    OutlinedTextField("${category.emoji} ${category.displayName}", {}, readOnly = true,
                        label = { Text("Categoria") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable, true), shape = RoundedCornerShape(12.dp))
                    ExposedDropdownMenu(expanded, { expanded = false }) {
                        NewsCategory.entries.forEach { cat ->
                            DropdownMenuItem(text = { Text("${cat.emoji} ${cat.displayName}") },
                                onClick = { category = cat; expanded = false })
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { if (title.isNotBlank()) onSave(title, summary, content, category) },
                colors = ButtonDefaults.buttonColors(containerColor = Plum40)) { Text("Salva") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Annulla") } }
    )
}
