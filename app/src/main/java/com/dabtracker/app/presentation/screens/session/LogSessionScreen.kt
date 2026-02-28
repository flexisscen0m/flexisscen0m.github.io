package com.dabtracker.app.presentation.screens.session

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun LogSessionScreen(
    app: DabTrackerApplication,
    preselectedExtractId: Long?,
    onNavigateBack: () -> Unit,
    onNavigateToRate: (Long) -> Unit,
    viewModel: LogSessionViewModel = viewModel(
        factory = LogSessionViewModel.Factory(
            app.extractRepository, app.sessionRepository,
            app.deviceRepository, preselectedExtractId
        )
    )
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val extracts by viewModel.extracts.collectAsStateWithLifecycle()
    val devices by viewModel.devices.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var customDevice by remember { mutableStateOf("") }

    LaunchedEffect(state.savedSessionId) {
        state.savedSessionId?.let { onNavigateToRate(it) }
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
                title = { Text("Log Dab Session") },
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
            Text("Select Extract", style = MaterialTheme.typography.labelLarge)
            if (extracts.isEmpty()) {
                Text(
                    "No extracts available. Add one first.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    extracts.forEach { extract ->
                        FilterChip(
                            selected = state.selectedExtractId == extract.id,
                            onClick = { viewModel.selectExtract(extract.id) },
                            label = {
                                Text("${extract.name} (%.2fg)".format(extract.remainingWeightGrams))
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = state.dabWeight,
                onValueChange = viewModel::updateDabWeight,
                label = { Text("Dab Weight") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                suffix = { Text("g") }
            )

            OutlinedTextField(
                value = state.temperature,
                onValueChange = viewModel::updateTemperature,
                label = { Text("Temperature") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                suffix = { Text("\u00B0C") }
            )

            Text("Device / Method", style = MaterialTheme.typography.labelLarge)
            if (devices.isNotEmpty()) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    devices.forEach { device ->
                        FilterChip(
                            selected = state.selectedDevice == device,
                            onClick = { viewModel.selectDevice(device) },
                            label = { Text(device) }
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = customDevice,
                    onValueChange = { customDevice = it },
                    label = { Text("Or type device name") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                Button(
                    onClick = {
                        if (customDevice.isNotBlank()) {
                            viewModel.selectDevice(customDevice.trim())
                        }
                    },
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text("Use")
                }
            }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = viewModel::logSession,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Log Session")
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}
