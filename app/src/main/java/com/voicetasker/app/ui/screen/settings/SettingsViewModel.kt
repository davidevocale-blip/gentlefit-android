package com.voicetasker.app.ui.screen.settings

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class SettingsUiState(
    val isPremium: Boolean = false,
    val themeMode: String = "system" // "light", "dark", "system"
)

@HiltViewModel
class SettingsViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun onThemeChanged(mode: String) { _uiState.update { it.copy(themeMode = mode) } }
    fun onPurchasePremium() { _uiState.update { it.copy(isPremium = true) } }
}
