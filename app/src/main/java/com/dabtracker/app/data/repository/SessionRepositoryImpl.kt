package com.dabtracker.app.data.repository

import com.dabtracker.app.data.local.dao.SessionDao
import com.dabtracker.app.data.local.entity.SessionEntity
import com.dabtracker.app.domain.model.Session
import com.dabtracker.app.domain.repository.SessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SessionRepositoryImpl(private val dao: SessionDao) : SessionRepository {

    override fun getAllSessions(): Flow<List<Session>> =
        dao.getAll().map { list -> list.map { it.toDomain() } }

    override fun getSessionsByExtract(extractId: Long): Flow<List<Session>> =
        dao.getByExtractId(extractId).map { list -> list.map { it.toDomain() } }

    override fun getLastSession(): Flow<Session?> =
        dao.getLastSession().map { it?.toDomain() }

    override fun getRecentSessions(limit: Int): Flow<List<Session>> =
        dao.getRecent(limit).map { list -> list.map { it.toDomain() } }

    override fun getSessionCount(): Flow<Int> = dao.getSessionCount()

    override fun getTotalDabWeight(): Flow<Double?> = dao.getTotalDabWeight()

    override suspend fun getSessionById(id: Long): Session? =
        dao.getById(id)?.toDomain()

    override suspend fun addSession(session: Session): Long =
        dao.insert(session.toEntity())

    override suspend fun updateSession(session: Session) =
        dao.update(session.toEntity())

    override suspend fun deleteSession(id: Long) =
        dao.deleteById(id)

    override fun getRawEntities(): Flow<List<SessionEntity>> = dao.getAll()

    override suspend fun insertAllRaw(entities: List<SessionEntity>) = dao.insertAll(entities)

    override suspend fun deleteAll() = dao.deleteAll()
}

private fun SessionEntity.toDomain() = Session(
    id = id,
    extractId = extractId,
    dabWeightGrams = dabWeightGrams,
    temperatureCelsius = temperatureCelsius,
    deviceName = deviceName,
    timestamp = timestamp,
    flavorRating = flavorRating,
    vaporQualityRating = vaporQualityRating,
    effectIntensityRating = effectIntensityRating,
    effectDurationRating = effectDurationRating,
    overallRating = overallRating,
    notes = notes
)

private fun Session.toEntity() = SessionEntity(
    id = id,
    extractId = extractId,
    dabWeightGrams = dabWeightGrams,
    temperatureCelsius = temperatureCelsius,
    deviceName = deviceName,
    timestamp = timestamp,
    flavorRating = flavorRating,
    vaporQualityRating = vaporQualityRating,
    effectIntensityRating = effectIntensityRating,
    effectDurationRating = effectDurationRating,
    overallRating = overallRating,
    notes = notes
)
