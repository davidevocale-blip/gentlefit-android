package com.voicetasker.app.ui.screen.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.voicetasker.app.domain.model.Category
import com.voicetasker.app.domain.usecase.category.*
import com.voicetasker.app.ui.theme.CategoryColors
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CategoriesUiState(
    val categories: List<Category> = emptyList(),
    val showAddDialog: Boolean = false,
    val editingCategory: Category? = null,
    val dialogName: String = "",
    val dialogColor: String = "#6C63FF",
    val dialogIcon: String = "Label",
    val errorMessage: String? = null
)

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val addCategoryUseCase: AddCategoryUseCase,
    private val updateCategoryUseCase: UpdateCategoryUseCase,
    private val deleteCategoryUseCase: DeleteCategoryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoriesUiState())
    val uiState: StateFlow<CategoriesUiState> = _uiState.asStateFlow()

    init { viewModelScope.launch { getCategoriesUseCase().collect { cats -> _uiState.update { it.copy(categories = cats) } } } }

    fun showAddDialog() { _uiState.update { it.copy(showAddDialog = true, editingCategory = null, dialogName = "", dialogColor = CategoryColors.random(), dialogIcon = "Label") } }
    fun showEditDialog(cat: Category) { _uiState.update { it.copy(showAddDialog = true, editingCategory = cat, dialogName = cat.name, dialogColor = cat.colorHex, dialogIcon = cat.iconName) } }
    fun dismissDialog() { _uiState.update { it.copy(showAddDialog = false, editingCategory = null, errorMessage = null) } }
    fun onDialogNameChanged(n: String) { _uiState.update { it.copy(dialogName = n) } }
    fun onDialogColorChanged(c: String) { _uiState.update { it.copy(dialogColor = c) } }

    fun saveCategory() {
        val state = _uiState.value
        if (state.dialogName.isBlank()) { _uiState.update { it.copy(errorMessage = "Il nome non può essere vuoto") }; return }
        viewModelScope.launch {
            if (state.editingCategory != null) {
                updateCategoryUseCase(state.editingCategory.copy(name = state.dialogName, colorHex = state.dialogColor, iconName = state.dialogIcon))
            } else {
                addCategoryUseCase(Category(name = state.dialogName, colorHex = state.dialogColor, iconName = state.dialogIcon, createdAt = System.currentTimeMillis()))
            }
            dismissDialog()
        }
    }

    fun deleteCategory(id: Long) { viewModelScope.launch { deleteCategoryUseCase(id) } }
}
