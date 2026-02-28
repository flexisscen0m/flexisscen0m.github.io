package com.dabtracker.app.presentation.screens.extract

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.dabtracker.app.domain.model.Extract
import com.dabtracker.app.domain.model.ExtractCategory
import com.dabtracker.app.domain.repository.ExtractRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AddEditExtractUiState(
    val name: String = "",
    val category: ExtractCategory = ExtractCategory.ROSIN,
    val customCategory: String = "",
    val strainName: String = "",
    val initialWeight: String = "",
    val purchaseDate: Long? = null,
    val notes: String = "",
    val isEditing: Boolean = false,
    val isSaved: Boolean = false,
    val errorMessage: String? = null
)

class AddEditExtractViewModel(
    private val extractRepository: ExtractRepository,
    private val extractId: Long?
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddEditExtractUiState())
    val uiState: StateFlow<AddEditExtractUiState> = _uiState.asStateFlow()

    init {
        if (extractId != null) {
            viewModelScope.launch {
                val extract = extractRepository.getExtractById(extractId).first()
                if (extract != null) {
                    val (cat, customName) = ExtractCategory.fromStringWithCustom(extract.categoryDisplayName)
                    _uiState.update {
                        it.copy(
                            name = extract.name,
                            category = cat,
                            customCategory = if (cat == ExtractCategory.CUSTOM) customName else "",
                            strainName = extract.strainName,
                            initialWeight = "%.2f".format(extract.initialWeightGrams),
                            purchaseDate = extract.purchaseDate,
                            notes = extract.notes ?: "",
                            isEditing = true
                        )
                    }
                }
            }
        }
    }

    fun updateName(value: String) = _uiState.update { it.copy(name = value) }
    fun updateCategory(value: ExtractCategory) = _uiState.update { it.copy(category = value) }
    fun updateCustomCategory(value: String) = _uiState.update { it.copy(customCategory = value) }
    fun updateStrainName(value: String) = _uiState.update { it.copy(strainName = value) }
    fun updateInitialWeight(value: String) = _uiState.update { it.copy(initialWeight = value) }
    fun updatePurchaseDate(value: Long?) = _uiState.update { it.copy(purchaseDate = value) }
    fun updateNotes(value: String) = _uiState.update { it.copy(notes = value) }

    fun save() {
        val state = _uiState.value
        if (state.name.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Name is required") }
            return
        }
        if (state.strainName.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Strain name is required") }
            return
        }
        val weight = state.initialWeight.toDoubleOrNull()
        if (weight == null || weight <= 0) {
            _uiState.update { it.copy(errorMessage = "Valid weight is required") }
            return
        }

        val categoryName = if (state.category == ExtractCategory.CUSTOM) {
            if (state.customCategory.isBlank()) {
                _uiState.update { it.copy(errorMessage = "Custom category name is required") }
                return
            }
            state.customCategory
        } else {
            state.category.displayName
        }

        viewModelScope.launch {
            val extract = Extract(
                id = extractId ?: 0,
                name = state.name.trim(),
                category = ExtractCategory.fromString(categoryName),
                categoryName = categoryName,
                strainName = state.strainName.trim(),
                initialWeightGrams = weight,
                purchaseDate = state.purchaseDate,
                notes = state.notes.trim().ifBlank { null }
            )
            if (state.isEditing && extractId != null) {
                extractRepository.updateExtract(extract)
            } else {
                extractRepository.addExtract(extract)
            }
            _uiState.update { it.copy(isSaved = true) }
        }
    }

    fun clearError() = _uiState.update { it.copy(errorMessage = null) }

    class Factory(
        private val extractRepository: ExtractRepository,
        private val extractId: Long?
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AddEditExtractViewModel(extractRepository, extractId) as T
        }
    }
}
