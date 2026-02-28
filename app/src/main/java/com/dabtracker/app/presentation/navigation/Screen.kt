package com.dabtracker.app.presentation.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object ExtractList : Screen("extracts")
    data object AddExtract : Screen("extracts/add")
    data object EditExtract : Screen("extracts/edit/{extractId}") {
        fun createRoute(extractId: Long) = "extracts/edit/$extractId"
    }
    data object SessionHistory : Screen("sessions")
    data object LogSession : Screen("sessions/log?extractId={extractId}") {
        fun createRoute(extractId: Long? = null) =
            if (extractId != null) "sessions/log?extractId=$extractId"
            else "sessions/log"
    }
    data object RateSession : Screen("sessions/rate/{sessionId}") {
        fun createRoute(sessionId: Long) = "sessions/rate/$sessionId"
    }
    data object Settings : Screen("settings")
}
