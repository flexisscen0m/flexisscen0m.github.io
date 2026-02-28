package com.dabtracker.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "sessions",
    foreignKeys = [
        ForeignKey(
            entity = ExtractEntity::class,
            parentColumns = ["id"],
            childColumns = ["extractId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("extractId")]
)
data class SessionEntity(
    @PrimaryKey(autoGenerate = true)
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
)
