package com.gentlefit.app.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gentlefit.app.data.ContentSeeder
import com.gentlefit.app.data.preferences.UserPreferences
import com.gentlefit.app.domain.model.DailyRoutine
import com.gentlefit.app.domain.repository.ProgressRepository
import com.gentlefit.app.domain.repository.RoutineRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class HomeUiState(
    val userName: String = "",
    val routine: DailyRoutine? = null,
    val streakDays: Int = 0,
    val completedDays: Int = 0,
    val quote: String = "",
    val isLoading: Boolean = true,
    val averageEnergy: Float = 0f,
    val averageSleep: Float = 0f,
    val weeklyCompletion: Float = 0f,
    val recentMoods: List<String> = emptyList(),
    val showCelebration: Boolean = false
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val routineRepository: RoutineRepository,
    private val progressRepository: ProgressRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    init { loadData() }

    private fun loadData() {
        viewModelScope.launch { userPreferences.userName.collect { name -> _uiState.update { it.copy(userName = name) } } }
        viewModelScope.launch {
            routineRepository.getTodayRoutine().collect { routine ->
                val dayOfYear = LocalDate.now().dayOfYear
                _uiState.update { it.copy(routine = routine, quote = ContentSeeder.getMotivationalQuote(dayOfYear), isLoading = false) }
            }
        }
        viewModelScope.launch { routineRepository.getCurrentStreak().collect { streak -> _uiState.update { it.copy(streakDays = streak) } } }
        viewModelScope.launch { routineRepository.getCompletedDaysCount().collect { count -> _uiState.update { it.copy(completedDays = count) } } }
        viewModelScope.launch { progressRepository.getAverageEnergy(7).collect { e -> _uiState.update { it.copy(averageEnergy = e) } } }
        viewModelScope.launch { progressRepository.getAverageSleep(7).collect { s -> _uiState.update { it.copy(averageSleep = s) } } }
        viewModelScope.launch {
            progressRepository.getRecentProgress(7).collect { entries ->
                val moods = entries.map { entry -> entry.mood.name }
                _uiState.update { state -> state.copy(recentMoods = moods) }
            }
        }
        // Weekly completion: routines completed this week / 7
        viewModelScope.launch {
            routineRepository.getCompletedDaysCount().collect { total ->
                val weekProgress = (total % 7) / 7f
                _uiState.update { it.copy(weeklyCompletion = weekProgress.coerceIn(0f, 1f)) }
            }
        }
    }

    fun completeExercise() {
        val id = _uiState.value.routine?.id ?: return
        viewModelScope.launch {
            routineRepository.completeExercise(id)
            checkCelebration()
        }
    }

    fun completeFoodTip() {
        val id = _uiState.value.routine?.id ?: return
        viewModelScope.launch {
            routineRepository.completeFoodTip(id)
            checkCelebration()
        }
    }

    fun completeGoal() {
        val id = _uiState.value.routine?.id ?: return
        viewModelScope.launch {
            routineRepository.completeGoal(id)
            checkCelebration()
        }
    }

    private fun checkCelebration() {
        val r = _uiState.value.routine ?: return
        if (r.completionCount >= 2) { // will be 3 after update
            _uiState.update { it.copy(showCelebration = true) }
            viewModelScope.launch {
                kotlinx.coroutines.delay(2000)
                _uiState.update { it.copy(showCelebration = false) }
            }
        }
    }

    fun dismissCelebration() { _uiState.update { it.copy(showCelebration = false) } }
}
