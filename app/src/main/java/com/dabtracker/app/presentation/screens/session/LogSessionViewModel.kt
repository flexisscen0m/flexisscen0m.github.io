package com.dabtracker.app.presentation.screens.session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.dabtracker.app.domain.model.Extract
import com.dabtracker.app.domain.model.Session
import com.dabtracker.app.domain.repository.DeviceRepository
import com.dabtracker.app.domain.repository.ExtractRepository
import com.dabtracker.app.domain.repository.SessionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LogSessionUiState(
    val selectedExtractId: Long? = null,
    val dabWeight: String = "",
    val temperature: String = "",
    val selectedDevice: String = "",
    val savedSessionId: Long? = null,
    val errorMessage: String? = null
)

class LogSessionViewModel(
    private val extractRepository: ExtractRepository,
    private val sessionRepository: SessionRepository,
    deviceRepository: DeviceRepository,
    preselectedExtractId: Long?
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        LogSessionUiState(selectedExtractId = preselectedExtractId)
    )
    val uiState: StateFlow<LogSessionUiState> = _uiState.asStateFlow()

    val extracts: StateFlow<List<Extract>> = extractRepository.getAllExtracts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val devices: StateFlow<List<String>> = deviceRepository.getAllDevices()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectExtract(id: Long) = _uiState.update { it.copy(selectedExtractId = id) }
    fun updateDabWeight(value: String) = _uiState.update { it.copy(dabWeight = value) }
    fun updateTemperature(value: String) = _uiState.update { it.copy(temperature = value) }
    fun selectDevice(value: String) = _uiState.update { it.copy(selectedDevice = value) }

    fun logSession() {
        val state = _uiState.value

        if (state.selectedExtractId == null) {
            _uiState.update { it.copy(errorMessage = "Select an extract") }
            return
        }
        val weight = state.dabWeight.toDoubleOrNull()
        if (weight == null || weight <= 0) {
            _uiState.update { it.copy(errorMessage = "Enter a valid dab weight") }
            return
        }
        val temp = state.temperature.toIntOrNull()
        if (temp == null || temp <= 0) {
            _uiState.update { it.copy(errorMessage = "Enter a valid temperature") }
            return
        }
        if (state.selectedDevice.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Select or enter a device") }
            return
        }

        viewModelScope.launch {
            val session = Session(
                extractId = state.selectedExtractId,
                dabWeightGrams = weight,
                temperatureCelsius = temp,
                deviceName = state.selectedDevice.trim()
            )
            val id = sessionRepository.addSession(session)
            _uiState.update { it.copy(savedSessionId = id) }
        }
    }

    fun clearError() = _uiState.update { it.copy(errorMessage = null) }

    class Factory(
        private val extractRepository: ExtractRepository,
        private val sessionRepository: SessionRepository,
        private val deviceRepository: DeviceRepository,
        private val preselectedExtractId: Long?
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return LogSessionViewModel(
                extractRepository, sessionRepository, deviceRepository, preselectedExtractId
            ) as T
        }
    }
}
