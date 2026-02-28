package com.dabtracker.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "extracts")
data class ExtractEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val category: String,
    val strainName: String,
    val initialWeightGrams: Double,
    val purchaseDate: Long? = null,
    val notes: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
