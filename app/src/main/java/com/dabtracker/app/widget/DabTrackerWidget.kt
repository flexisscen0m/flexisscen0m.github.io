package com.dabtracker.app.widget

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.dabtracker.app.data.local.AppDatabase
import com.dabtracker.app.data.local.dao.ExtractWithRemaining
import com.dabtracker.app.data.local.entity.SessionEntity
import com.dabtracker.app.presentation.MainActivity
import kotlinx.coroutines.flow.first
import java.util.concurrent.TimeUnit

class DabTrackerWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val db = AppDatabase.getInstance(context)

        val extracts = db.extractDao().getAllWithRemaining().first()
        val activeExtract = extracts.maxByOrNull { it.remainingWeightGrams }
        val lastSession = db.sessionDao().getLastSession().first()
        val lastExtractName = if (lastSession != null) {
            extracts.find { it.id == lastSession.extractId }?.name ?: "Unknown"
        } else null

        provideContent {
            GlanceTheme {
                WidgetContent(
                    activeExtract = activeExtract,
                    lastSession = lastSession,
                    lastExtractName = lastExtractName
                )
            }
        }
    }
}

@Composable
private fun WidgetContent(
    activeExtract: ExtractWithRemaining?,
    lastSession: SessionEntity?,
    lastExtractName: String?
) {
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(GlanceTheme.colors.surface)
            .padding(12.dp)
            .clickable(actionStartActivity<MainActivity>()),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = "Dab Tracker",
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = GlanceTheme.colors.primary
            )
        )

        Spacer(GlanceModifier.height(8.dp))

        if (activeExtract != null) {
            Text(
                text = activeExtract.name,
                style = TextStyle(
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    color = GlanceTheme.colors.onSurface
                )
            )
            Text(
                text = "%.2fg remaining".format(activeExtract.remainingWeightGrams),
                style = TextStyle(
                    fontSize = 13.sp,
                    color = GlanceTheme.colors.onSurfaceVariant
                )
            )
        } else {
            Text(
                text = "No extracts yet",
                style = TextStyle(
                    fontSize = 13.sp,
                    color = GlanceTheme.colors.onSurfaceVariant
                )
            )
        }

        Spacer(GlanceModifier.height(8.dp))

        if (lastSession != null && lastExtractName != null) {
            Text(
                text = "Last: $lastExtractName",
                style = TextStyle(
                    fontSize = 12.sp,
                    color = GlanceTheme.colors.onSurfaceVariant
                )
            )
            Text(
                text = formatTimeAgo(lastSession.timestamp),
                style = TextStyle(
                    fontSize = 11.sp,
                    color = GlanceTheme.colors.onSurfaceVariant
                )
            )
        }

        Spacer(GlanceModifier.height(8.dp).defaultWeight())

        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Tap to log dab",
                style = TextStyle(
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp,
                    color = GlanceTheme.colors.primary
                ),
                modifier = GlanceModifier
                    .clickable(actionStartActivity<MainActivity>())
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            )
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
        else -> {
            val days = TimeUnit.MILLISECONDS.toDays(diff)
            "${days}d ago"
        }
    }
}
