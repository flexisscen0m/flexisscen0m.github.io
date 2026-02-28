package com.dabtracker.app.domain.repository

import com.dabtracker.app.data.local.entity.ExtractEntity
import com.dabtracker.app.domain.model.Extract
import kotlinx.coroutines.flow.Flow

interface ExtractRepository {
    fun getAllExtracts(): Flow<List<Extract>>
    fun getExtractById(id: Long): Flow<Extract?>
    fun getExtractsByCategory(category: String): Flow<List<Extract>>
    fun getAllCategories(): Flow<List<String>>
    suspend fun addExtract(extract: Extract): Long
    suspend fun updateExtract(extract: Extract)
    suspend fun deleteExtract(id: Long)
    suspend fun getRawEntity(id: Long): ExtractEntity?
    fun getRawEntities(): Flow<List<ExtractEntity>>
    suspend fun insertAllRaw(entities: List<ExtractEntity>)
}
