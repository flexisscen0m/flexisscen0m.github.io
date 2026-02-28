package com.dabtracker.app.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.dabtracker.app.DabTrackerApplication
import com.dabtracker.app.presentation.navigation.DabTrackerNavGraph
import com.dabtracker.app.presentation.theme.DabTrackerTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as DabTrackerApplication

        setContent {
            DabTrackerTheme {
                DabTrackerNavGraph(app = app)
            }
        }
    }
}
