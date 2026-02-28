package com.dabtracker.app.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.dabtracker.app.domain.model.Extract
import com.dabtracker.app.domain.model.Session
import com.dabtracker.app.domain.repository.ExtractRepository
import com.dabtracker.app.domain.repository.SessionRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class HomeUiState(
    val recentSessions: List<Pair<Session, Extract?>> = emptyList(),
    val extracts: List<Extract> = emptyList(),
    val sessionCount: Int = 0,
    val totalDabWeight: Double = 0.0,
    val lastSession: Session? = null
)

class HomeViewModel(
    extractRepository: ExtractRepository,
    sessionRepository: SessionRepository
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = combine(
        sessionRepository.getRecentSessions(10),
        extractRepository.getAllExtracts(),
        sessionRepository.getSessionCount(),
        sessionRepository.getTotalDabWeight()
    ) { sessions, extracts, count, totalWeight ->
        val extractMap = extracts.associateBy { it.id }
        HomeUiState(
            recentSessions = sessions.map { it to extractMap[it.extractId] },
            extracts = extracts,
            sessionCount = count,
            totalDabWeight = totalWeight ?: 0.0,
            lastSession = sessions.firstOrNull()
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState()
    )

    class Factory(
        private val extractRepository: ExtractRepository,
        private val sessionRepository: SessionRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HomeViewModel(extractRepository, sessionRepository) as T
        }
    }
}
