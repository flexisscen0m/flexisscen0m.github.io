package com.dabtracker.app.presentation.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dabtracker.app.DabTrackerApplication
import com.dabtracker.app.domain.model.Extract
import com.dabtracker.app.domain.model.Session
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

@Composable
fun HomeScreen(
    app: DabTrackerApplication,
    onNavigateToLogSession: (Long?) -> Unit,
    onNavigateToExtracts: () -> Unit,
    onNavigateToSessions: () -> Unit,
    viewModel: HomeViewModel = viewModel(
        factory = HomeViewModel.Factory(app.extractRepository, app.sessionRepository)
    )
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { onNavigateToLogSession(null) },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Log Dab") }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Dab Tracker",
                    style = MaterialTheme.typography.headlineLarge
                )
                Spacer(Modifier.height(4.dp))
            }

            item {
                StatsRow(
                    sessionCount = state.sessionCount,
                    totalWeight = state.totalDabWeight,
                    extractCount = state.extracts.size
                )
            }

            if (state.extracts.isNotEmpty()) {
                item {
                    SectionHeader("Active Extracts", onSeeAll = onNavigateToExtracts)
                }
                items(state.extracts.take(3), key = { it.id }) { extract ->
                    ActiveExtractCard(
                        extract = extract,
                        onClick = { onNavigateToLogSession(extract.id) }
                    )
                }
            }

            if (state.recentSessions.isNotEmpty()) {
                item {
                    SectionHeader("Recent Sessions", onSeeAll = onNavigateToSessions)
                }
                items(state.recentSessions.take(5), key = { it.first.id }) { (session, extract) ->
                    RecentSessionCard(session = session, extractName = extract?.name ?: "Unknown")
                }
            }

            if (state.extracts.isEmpty() && state.recentSessions.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Welcome to Dab Tracker",
                                style = MaterialTheme.typography.titleLarge
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = "Start by adding an extract to your library, then log your first session.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Spacer(Modifier.height(12.dp))
                            TextButton(onClick = onNavigateToExtracts) {
                                Text("Add First Extract")
                            }
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}

@Composable
private fun StatsRow(sessionCount: Int, totalWeight: Double, extractCount: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        StatCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.LocalFireDepartment,
            value = "$sessionCount",
            label = "Sessions"
        )
        StatCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.Scale,
            value = "%.2fg".format(totalWeight),
            label = "Total Used"
        )
        StatCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.Tag,
            value = "$extractCount",
            label = "Extracts"
        )
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Icon(
                icon, contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(4.dp))
            Text(text = value, style = MaterialTheme.typography.titleMedium)
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SectionHeader(title: String, onSeeAll: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, style = MaterialTheme.typography.titleMedium)
        TextButton(onClick = onSeeAll) { Text("See all") }
    }
}

@Composable
private fun ActiveExtractCard(extract: Extract, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = extract.name, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "${extract.categoryDisplayName} - ${extract.strainName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "%.2fg".format(extract.remainingWeightGrams),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "remaining",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun RecentSessionCard(session: Session, extractName: String) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = extractName, style = MaterialTheme.typography.bodyLarge)
                Text(
                    text = "${session.deviceName} @ ${session.temperatureCelsius}\u00B0C",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "%.2fg".format(session.dabWeightGrams),
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = formatTimeAgo(session.timestamp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun formatTimeAgo(timestamp: Long): String {
    val diff = System.currentTimeMillis() - timestamp
    return when {
        diff < TimeUnit.MINUTES.toMillis(1) -> "Just now"
        diff < TimeUnit.HOURS.toMillis(1) -> {
            val mins = TimeUnit.MILLISECONDS.toMinutes(diff)
            "${mins}m ago"
        }
        diff < TimeUnit.DAYS.toMillis(1) -> {
            val hours = TimeUnit.MILLISECONDS.toHours(diff)
            "${hours}h ago"
        }
        diff < TimeUnit.DAYS.toMillis(7) -> {
            val days = TimeUnit.MILLISECONDS.toDays(diff)
            "${days}d ago"
        }
        else -> SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(timestamp))
    }
}
