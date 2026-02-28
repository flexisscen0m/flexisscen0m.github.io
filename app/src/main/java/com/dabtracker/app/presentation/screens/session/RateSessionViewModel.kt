package com.dabtracker.app.presentation.screens.session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.dabtracker.app.domain.model.Session
import com.dabtracker.app.domain.repository.SessionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RateSessionUiState(
    val session: Session? = null,
    val flavor: Int = 0,
    val vaporQuality: Int = 0,
    val effectIntensity: Int = 0,
    val effectDuration: Int = 0,
    val overall: Int = 0,
    val notes: String = "",
    val isSaved: Boolean = false
)

class RateSessionViewModel(
    private val sessionRepository: SessionRepository,
    private val sessionId: Long
) : ViewModel() {

    private val _uiState = MutableStateFlow(RateSessionUiState())
    val uiState: StateFlow<RateSessionUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val session = sessionRepository.getSessionById(sessionId)
            if (session != null) {
                _uiState.update {
                    it.copy(
                        session = session,
                        flavor = session.flavorRating ?: 0,
                        vaporQuality = session.vaporQualityRating ?: 0,
                        effectIntensity = session.effectIntensityRating ?: 0,
                        effectDuration = session.effectDurationRating ?: 0,
                        overall = session.overallRating ?: 0,
                        notes = session.notes ?: ""
                    )
                }
            }
        }
    }

    fun updateFlavor(v: Int) = _uiState.update { it.copy(flavor = v) }
    fun updateVaporQuality(v: Int) = _uiState.update { it.copy(vaporQuality = v) }
    fun updateEffectIntensity(v: Int) = _uiState.update { it.copy(effectIntensity = v) }
    fun updateEffectDuration(v: Int) = _uiState.update { it.copy(effectDuration = v) }
    fun updateOverall(v: Int) = _uiState.update { it.copy(overall = v) }
    fun updateNotes(v: String) = _uiState.update { it.copy(notes = v) }

    fun save() {
        val state = _uiState.value
        val session = state.session ?: return

        viewModelScope.launch {
            sessionRepository.updateSession(
                session.copy(
                    flavorRating = state.flavor.takeIf { it > 0 },
                    vaporQualityRating = state.vaporQuality.takeIf { it > 0 },
                    effectIntensityRating = state.effectIntensity.takeIf { it > 0 },
                    effectDurationRating = state.effectDuration.takeIf { it > 0 },
                    overallRating = state.overall.takeIf { it > 0 },
                    notes = state.notes.trim().ifBlank { null }
                )
            )
            _uiState.update { it.copy(isSaved = true) }
        }
    }

    fun skip() {
        _uiState.update { it.copy(isSaved = true) }
    }

    class Factory(
        private val sessionRepository: SessionRepository,
        private val sessionId: Long
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return RateSessionViewModel(sessionRepository, sessionId) as T
        }
    }
}
