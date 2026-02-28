package com.dabtracker.app.domain.model

data class Session(
    val id: Long = 0,
    val extractId: Long,
    val dabWeightGrams: Double,
    val temperatureCelsius: Int,
    val deviceName: String,
    val timestamp: Long = System.currentTimeMillis(),
    val flavorRating: Int? = null,
    val vaporQualityRating: Int? = null,
    val effectIntensityRating: Int? = null,
    val effectDurationRating: Int? = null,
    val overallRating: Int? = null,
    val notes: String? = null
) {
    val isRated: Boolean
        get() = overallRating != null

    val averageRating: Float?
        get() {
            val ratings = listOfNotNull(
                flavorRating, vaporQualityRating,
                effectIntensityRating, effectDurationRating, overallRating
            )
            return if (ratings.isNotEmpty()) ratings.average().toFloat() else null
        }
}
