package com.dabtracker.app.presentation.screens.extract

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.dabtracker.app.domain.model.Extract
import com.dabtracker.app.domain.repository.ExtractRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class SortOption(val label: String) {
    NAME("Name"),
    DATE("Date Added"),
    REMAINING("Remaining Weight")
}

data class ExtractListUiState(
    val extracts: List<Extract> = emptyList(),
    val categories: List<String> = emptyList(),
    val selectedCategory: String? = null,
    val sortOption: SortOption = SortOption.NAME
)

class ExtractListViewModel(
    private val extractRepository: ExtractRepository
) : ViewModel() {

    private val _selectedCategory = MutableStateFlow<String?>(null)
    private val _sortOption = MutableStateFlow(SortOption.NAME)

    val uiState: StateFlow<ExtractListUiState> = combine(
        extractRepository.getAllExtracts(),
        extractRepository.getAllCategories(),
        _selectedCategory,
        _sortOption
    ) { extracts, categories, selectedCat, sortOpt ->
        val filtered = if (selectedCat != null) {
            extracts.filter { it.categoryDisplayName == selectedCat }
        } else extracts

        val sorted = when (sortOpt) {
            SortOption.NAME -> filtered.sortedBy { it.name.lowercase() }
            SortOption.DATE -> filtered.sortedByDescending { it.createdAt }
            SortOption.REMAINING -> filtered.sortedByDescending { it.remainingWeightGrams }
        }

        ExtractListUiState(
            extracts = sorted,
            categories = categories,
            selectedCategory = selectedCat,
            sortOption = sortOpt
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ExtractListUiState()
    )

    fun setCategory(category: String?) {
        _selectedCategory.value = category
    }

    fun setSortOption(option: SortOption) {
        _sortOption.value = option
    }

    fun deleteExtract(id: Long) {
        viewModelScope.launch {
            extractRepository.deleteExtract(id)
        }
    }

    class Factory(
        private val extractRepository: ExtractRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ExtractListViewModel(extractRepository) as T
        }
    }
}
