package com.dabtracker.app.presentation.screens.session

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dabtracker.app.DabTrackerApplication
import com.dabtracker.app.presentation.components.StarRating

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RateSessionScreen(
    app: DabTrackerApplication,
    sessionId: Long,
    onNavigateBack: () -> Unit,
    viewModel: RateSessionViewModel = viewModel(
        factory = RateSessionViewModel.Factory(app.sessionRepository, sessionId)
    )
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(state.isSaved) {
        if (state.isSaved) onNavigateBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Rate Session") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "How was this session?",
                style = MaterialTheme.typography.headlineMedium
            )

            state.session?.let { session ->
                Text(
                    text = "%.2fg at %d\u00B0C via %s".format(
                        session.dabWeightGrams,
                        session.temperatureCelsius,
                        session.deviceName
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.height(4.dp))

            StarRating(
                label = "Flavor",
                rating = state.flavor,
                onRatingChange = viewModel::updateFlavor
            )

            StarRating(
                label = "Vapor Quality",
                rating = state.vaporQuality,
                onRatingChange = viewModel::updateVaporQuality
            )

            StarRating(
                label = "Effect Intensity",
                rating = state.effectIntensity,
                onRatingChange = viewModel::updateEffectIntensity
            )

            StarRating(
                label = "Effect Duration",
                rating = state.effectDuration,
                onRatingChange = viewModel::updateEffectDuration
            )

            StarRating(
                label = "Overall Satisfaction",
                rating = state.overall,
                onRatingChange = viewModel::updateOverall
            )

            OutlinedTextField(
                value = state.notes,
                onValueChange = viewModel::updateNotes,
                label = { Text("Session notes (optional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 6
            )

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = viewModel::save,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Rating")
            }

            OutlinedButton(
                onClick = viewModel::skip,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Skip Rating")
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}
