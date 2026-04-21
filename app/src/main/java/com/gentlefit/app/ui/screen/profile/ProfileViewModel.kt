package com.gentlefit.app.ui.screen.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gentlefit.app.data.local.dao.RoutineDao
import com.gentlefit.app.data.preferences.UserPreferences
import com.gentlefit.app.domain.repository.RoutineRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject

data class ProfileUiState(
    val userName: String = "",
    val userGoal: String = "",
    val showWeight: Boolean = false,
    val darkMode: Boolean = false,
    val notifications: Boolean = true,
    val completedDays: Int = 0,
    val streakDays: Int = 0,
    val height: Float = 0f,
    val bodyType: String = "Normale",
    val photoUri: String = "",
    val currentWeight: Float = 0f,
    val lastWeightDate: String = "",
    val weeklyUsageCount: Int = 0,
    val isEditingProfile: Boolean = false,
    val canRecordWeight: Boolean = false,
    val weightBlockReason: String = ""
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userPreferences: UserPreferences,
    private val routineRepository: RoutineRepository,
    private val routineDao: RoutineDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch { userPreferences.userName.collect { n -> _uiState.update { it.copy(userName = n) } } }
        viewModelScope.launch { userPreferences.userGoal.collect { g -> _uiState.update { it.copy(userGoal = g) } } }
        viewModelScope.launch { userPreferences.showWeight.collect { s -> _uiState.update { it.copy(showWeight = s) } } }
        viewModelScope.launch { userPreferences.darkMode.collect { d -> _uiState.update { it.copy(darkMode = d) } } }
        viewModelScope.launch { userPreferences.notificationsEnabled.collect { n -> _uiState.update { it.copy(notifications = n) } } }
        viewModelScope.launch { routineRepository.getCompletedDaysCount().collect { c -> _uiState.update { it.copy(completedDays = c) } } }
        viewModelScope.launch { routineRepository.getCurrentStreak().collect { s -> _uiState.update { it.copy(streakDays = s) } } }
        viewModelScope.launch { userPreferences.userHeight.collect { h -> _uiState.update { it.copy(height = h) } } }
        viewModelScope.launch { userPreferences.userBodyType.collect { b -> _uiState.update { it.copy(bodyType = b) } } }
        viewModelScope.launch { userPreferences.userPhotoUri.collect { p -> _uiState.update { it.copy(photoUri = p) } } }
        viewModelScope.launch { userPreferences.userWeight.collect { w -> _uiState.update { it.copy(currentWeight = w) } } }
        viewModelScope.launch { userPreferences.lastWeightDate.collect { d -> _uiState.update { it.copy(lastWeightDate = d) } } }
        loadWeeklyUsage()
    }

    private fun loadWeeklyUsage() {
        val startOfWeek = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).toString()
        viewModelScope.launch {
            routineDao.getUsageCountSince(startOfWeek).collect { count ->
                val lastDate = _uiState.value.lastWeightDate
                val alreadyRecordedThisWeek = lastDate.isNotBlank() && lastDate >= startOfWeek
                val canRecord = count >= 4 && !alreadyRecordedThisWeek
                val reason = when {
                    alreadyRecordedThisWeek -> "Hai già registrato il peso questa settimana 🌟"
                    count < 4 -> "Usa l'app ancora ${4 - count} volta/e questa settimana per sbloccare (${count}/4)"
                    else -> ""
                }
                _uiState.update { it.copy(weeklyUsageCount = count, canRecordWeight = canRecord, weightBlockReason = reason) }
            }
        }
    }

    fun toggleEditProfile() { _uiState.update { it.copy(isEditingProfile = !it.isEditingProfile) } }
    fun toggleWeight(show: Boolean) { viewModelScope.launch { userPreferences.setShowWeight(show) } }
    fun toggleDarkMode(enabled: Boolean) { viewModelScope.launch { userPreferences.setDarkMode(enabled) } }
    fun toggleNotifications(enabled: Boolean) { viewModelScope.launch { userPreferences.setNotificationsEnabled(enabled) } }
    fun updateName(name: String) { viewModelScope.launch { userPreferences.setUserName(name) } }
    fun updateGoal(goal: String) { viewModelScope.launch { userPreferences.setUserGoal(goal) } }
    fun updateHeight(height: Float) { viewModelScope.launch { userPreferences.setUserHeight(height) } }
    fun updateBodyType(type: String) { viewModelScope.launch { userPreferences.setUserBodyType(type) } }
    fun updatePhotoUri(uri: String) { viewModelScope.launch { userPreferences.setUserPhotoUri(uri) } }

    fun recordWeight(weight: Float) {
        if (!_uiState.value.canRecordWeight) return
        viewModelScope.launch {
            userPreferences.setUserWeight(weight)
            userPreferences.setLastWeightDate(LocalDate.now().toString())
            _uiState.update { it.copy(canRecordWeight = false, currentWeight = weight, weightBlockReason = "Hai già registrato il peso questa settimana 🌟") }
        }
    }
}
