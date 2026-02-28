package com.dabtracker.app.data.repository

import com.dabtracker.app.data.local.dao.ExtractDao
import com.dabtracker.app.data.local.dao.ExtractWithRemaining
import com.dabtracker.app.data.local.entity.ExtractEntity
import com.dabtracker.app.domain.model.Extract
import com.dabtracker.app.domain.model.ExtractCategory
import com.dabtracker.app.domain.repository.ExtractRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ExtractRepositoryImpl(private val dao: ExtractDao) : ExtractRepository {

    override fun getAllExtracts(): Flow<List<Extract>> =
        dao.getAllWithRemaining().map { list -> list.map { it.toDomain() } }

    override fun getExtractById(id: Long): Flow<Extract?> =
        dao.getByIdWithRemaining(id).map { it?.toDomain() }

    override fun getExtractsByCategory(category: String): Flow<List<Extract>> =
        dao.getByCategory(category).map { list -> list.map { it.toDomain() } }

    override fun getAllCategories(): Flow<List<String>> = dao.getAllCategories()

    override suspend fun addExtract(extract: Extract): Long {
        return dao.insert(extract.toEntity())
    }

    override suspend fun updateExtract(extract: Extract) {
        dao.update(extract.toEntity())
    }

    override suspend fun deleteExtract(id: Long) {
        dao.deleteById(id)
    }

    override suspend fun getRawEntity(id: Long): ExtractEntity? = dao.getById(id)

    override fun getRawEntities(): Flow<List<ExtractEntity>> = dao.getAll()

    override suspend fun insertAllRaw(entities: List<ExtractEntity>) = dao.insertAll(entities)
}

private fun ExtractWithRemaining.toDomain() = Extract(
    id = id,
    name = name,
    category = ExtractCategory.fromString(category),
    categoryName = category,
    strainName = strainName,
    initialWeightGrams = initialWeightGrams,
    remainingWeightGrams = remainingWeightGrams,
    purchaseDate = purchaseDate,
    notes = notes,
    createdAt = createdAt
)

private fun Extract.toEntity() = ExtractEntity(
    id = id,
    name = name,
    category = categoryName,
    strainName = strainName,
    initialWeightGrams = initialWeightGrams,
    purchaseDate = purchaseDate,
    notes = notes,
    createdAt = createdAt
)
