package com.dabtracker.app.domain.model

data class Extract(
    val id: Long = 0,
    val name: String,
    val category: ExtractCategory,
    val categoryName: String,
    val strainName: String,
    val initialWeightGrams: Double,
    val remainingWeightGrams: Double = initialWeightGrams,
    val purchaseDate: Long? = null,
    val notes: String? = null,
    val createdAt: Long = System.currentTimeMillis()
) {
    val categoryDisplayName: String
        get() = categoryName
}
