package com.gentlefit.app.ui.screen.coach

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gentlefit.app.data.preferences.UserPreferences
import com.gentlefit.app.domain.repository.CoachRepository
import com.gentlefit.app.domain.repository.RoutineRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalTime
import javax.inject.Inject

data class CoachUiState(
    val greeting: String = "",
    val currentResponse: String? = null,
    val selectedOption: String? = null,
    val showOptions: Boolean = true,
    val options: List<String> = listOf("Mi sento bene 😊", "Oggi è dura 😔", "Ho bisogno di motivazione 💪", "Raccontami qualcosa 🧠")
)

@HiltViewModel
class CoachViewModel @Inject constructor(
    private val coachRepository: CoachRepository,
    private val routineRepository: RoutineRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(CoachUiState())
    val uiState = _uiState.asStateFlow()

    init { loadGreeting() }

    private fun loadGreeting() {
        viewModelScope.launch {
            val name = userPreferences.userName.first()
            val streak = routineRepository.getCurrentStreak().first()
            val greeting = coachRepository.getGreeting(name, LocalTime.now().hour, streak)
            _uiState.update { it.copy(greeting = greeting.text) }
        }
    }

    fun selectOption(option: String) {
        val cleanOption = option.replace(Regex("\\s*[😊😔💪🧠]\\s*$"), "").trim()
        viewModelScope.launch {
            val response = coachRepository.getQuickReplyResponse(cleanOption)
            _uiState.update {
                it.copy(currentResponse = response.text, selectedOption = option, showOptions = false)
            }
        }
    }

    fun resetOptions() {
        _uiState.update { it.copy(currentResponse = null, selectedOption = null, showOptions = true) }
    }
}
