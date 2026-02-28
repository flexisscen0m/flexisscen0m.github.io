package com.dabtracker.app.presentation.screens.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.dabtracker.app.data.export.ExportImportManager
import com.dabtracker.app.domain.repository.DeviceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SettingsUiState(
    val newDeviceName: String = "",
    val message: String? = null,
    val exportJson: String? = null
)

class SettingsViewModel(
    private val deviceRepository: DeviceRepository,
    private val exportImportManager: ExportImportManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    val devices: StateFlow<List<String>> = deviceRepository.getAllDevices()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateNewDeviceName(name: String) = _uiState.update { it.copy(newDeviceName = name) }

    fun addDevice() {
        val name = _uiState.value.newDeviceName.trim()
        if (name.isBlank()) return
        viewModelScope.launch {
            deviceRepository.addDevice(name)
            _uiState.update { it.copy(newDeviceName = "", message = "Device added") }
        }
    }

    fun exportData() {
        viewModelScope.launch {
            try {
                val json = exportImportManager.exportToJson()
                _uiState.update { it.copy(exportJson = json, message = "Export ready") }
            } catch (e: Exception) {
                _uiState.update { it.copy(message = "Export failed: ${e.message}") }
            }
        }
    }

    fun shareExport(context: Context) {
        val json = _uiState.value.exportJson ?: return
        try {
            val file = java.io.File(context.cacheDir, "dab_tracker_export.json")
            file.writeText(json)
            val uri = androidx.core.content.FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/json"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, "Share Export"))
        } catch (e: Exception) {
            _uiState.update { it.copy(message = "Share failed: ${e.message}") }
        }
    }

    fun importData(context: Context, uri: Uri) {
        viewModelScope.launch {
            try {
                val json = context.contentResolver.openInputStream(uri)
                    ?.bufferedReader()?.readText()
                    ?: throw IllegalStateException("Could not read file")
                exportImportManager.importFromJson(json)
                _uiState.update { it.copy(message = "Import successful") }
            } catch (e: Exception) {
                _uiState.update { it.copy(message = "Import failed: ${e.message}") }
            }
        }
    }

    fun clearMessage() = _uiState.update { it.copy(message = null) }

    class Factory(
        private val deviceRepository: DeviceRepository,
        private val exportImportManager: ExportImportManager
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SettingsViewModel(deviceRepository, exportImportManager) as T
        }
    }
}
