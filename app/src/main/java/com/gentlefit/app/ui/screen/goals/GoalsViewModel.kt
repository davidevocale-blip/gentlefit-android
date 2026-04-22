package com.gentlefit.app.ui.screen.goals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gentlefit.app.domain.model.MicroGoal
import com.gentlefit.app.domain.repository.GoalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class GoalsViewModel @Inject constructor(
    private val goalRepository: GoalRepository
) : ViewModel() {

    private val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)

    val activeGoals: StateFlow<List<MicroGoal>> = goalRepository.getActiveGoals()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Only show goals completed TODAY, hide previous days' completed goals
    val completedGoals: StateFlow<List<MicroGoal>> = goalRepository.getCompletedGoals()
        .map { goals -> goals.filter { it.completedDate == today } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val suggestedGoals: StateFlow<List<MicroGoal>> = goalRepository.getSuggestedGoals()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun completeGoal(goalId: Long) {
        viewModelScope.launch {
            goalRepository.completeGoal(goalId, today)
        }
    }

    fun addGoal(goal: MicroGoal) {
        viewModelScope.launch { goalRepository.addGoal(goal) }
    }

    fun deleteGoal(goalId: Long) {
        viewModelScope.launch { goalRepository.deleteGoal(goalId) }
    }
}
