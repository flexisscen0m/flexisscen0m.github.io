package com.dabtracker.app.presentation.screens.extract

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dabtracker.app.DabTrackerApplication
import com.dabtracker.app.domain.model.ExtractCategory

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddEditExtractScreen(
    app: DabTrackerApplication,
    extractId: Long?,
    onNavigateBack: () -> Unit,
    viewModel: AddEditExtractViewModel = viewModel(
        factory = AddEditExtractViewModel.Factory(
            app.extractRepository, app.customCategoryManager, extractId
        )
    )
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val customCategories by viewModel.customCategories.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var categoryToDelete by remember { mutableStateOf<String?>(null) }

    categoryToDelete?.let { name ->
        AlertDialog(
            onDismissRequest = { categoryToDelete = null },
            title = { Text("Delete Category") },
            text = { Text("Delete custom category \"$name\"? Existing extracts will keep their category.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteCustomCategory(name)
                    categoryToDelete = null
                }) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { categoryToDelete = null }) { Text("Cancel") }
            }
        )
    }

    LaunchedEffect(state.isSaved) {
        if (state.isSaved) onNavigateBack()
    }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (state.isEditing) "Edit Extract" else "Add Extract") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = state.name,
                onValueChange = viewModel::updateName,
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Text("Category", style = MaterialTheme.typography.labelLarge)
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                ExtractCategory.defaults.forEach { cat ->
                    FilterChip(
                        selected = state.category == cat && state.selectedCustomCategory == null,
                        onClick = { viewModel.updateCategory(cat) },
                        label = { Text(cat.displayName) }
                    )
                }

                customCategories.forEach { customName ->
                    FilterChip(
                        selected = state.selectedCustomCategory == customName,
                        onClick = { viewModel.selectCustomCategory(customName) },
                        label = { Text(customName) },
                        trailingIcon = {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Delete $customName",
                                modifier = Modifier
                                    .size(16.dp)
                                    .clickable { categoryToDelete = customName }
                            )
                        }
                    )
                }

                FilterChip(
                    selected = state.category == ExtractCategory.CUSTOM && state.selectedCustomCategory == null,
                    onClick = { viewModel.updateCategory(ExtractCategory.CUSTOM) },
                    label = { Text("+ New") }
                )
            }

            if (state.category == ExtractCategory.CUSTOM && state.selectedCustomCategory == null) {
                OutlinedTextField(
                    value = state.customCategory,
                    onValueChange = viewModel::updateCustomCategory,
                    label = { Text("Custom Category Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            OutlinedTextField(
                value = state.strainName,
                onValueChange = viewModel::updateStrainName,
                label = { Text("Strain Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = state.initialWeight,
                onValueChange = viewModel::updateInitialWeight,
                label = { Text("Initial Weight (grams)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                suffix = { Text("g") }
            )

            OutlinedTextField(
                value = state.notes,
                onValueChange = viewModel::updateNotes,
                label = { Text("Notes (optional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = viewModel::save,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (state.isEditing) "Update Extract" else "Add Extract")
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}
