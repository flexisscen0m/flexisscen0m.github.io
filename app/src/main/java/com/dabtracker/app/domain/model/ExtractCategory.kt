package com.dabtracker.app.domain.model

enum class ExtractCategory(val displayName: String) {
    ROSIN("Rosin"),
    LIVE_RESIN("Live Resin"),
    BHO("BHO"),
    BUBBLE_HASH("Bubble Hash"),
    CUSTOM("Custom");

    companion object {
        val defaults = listOf(ROSIN, LIVE_RESIN, BHO, BUBBLE_HASH)

        fun fromString(value: String): ExtractCategory {
            return entries.find { it.displayName.equals(value, ignoreCase = true) }
                ?: CUSTOM.also { /* Custom category - displayName won't match */ }
        }

        fun fromStringWithCustom(value: String): Pair<ExtractCategory, String> {
            val found = entries.find { it.displayName.equals(value, ignoreCase = true) }
            return if (found != null && found != CUSTOM) {
                found to found.displayName
            } else {
                CUSTOM to value
            }
        }
    }
}
