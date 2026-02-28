package com.dabtracker.app.presentation.screens.session

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
import kotlinx.coroutines.launch

data class SessionWithExtract(
    val session: Session,
    val extract: Extract?
)

data class SessionHistoryUiState(
    val sessions: List<SessionWithExtract> = emptyList()
)

class SessionHistoryViewModel(
    private val sessionRepository: SessionRepository,
    extractRepository: ExtractRepository
) : ViewModel() {

    val uiState: StateFlow<SessionHistoryUiState> = combine(
        sessionRepository.getAllSessions(),
        extractRepository.getAllExtracts()
    ) { sessions, extracts ->
        val extractMap = extracts.associateBy { it.id }
        SessionHistoryUiState(
            sessions = sessions.map { SessionWithExtract(it, extractMap[it.extractId]) }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SessionHistoryUiState()
    )

    fun deleteSession(id: Long) {
        viewModelScope.launch {
            sessionRepository.deleteSession(id)
        }
    }

    class Factory(
        private val sessionRepository: SessionRepository,
        private val extractRepository: ExtractRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SessionHistoryViewModel(sessionRepository, extractRepository) as T
        }
    }
}
