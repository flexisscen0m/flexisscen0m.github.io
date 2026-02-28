package com.dabtracker.app.domain.repository

import com.dabtracker.app.data.local.entity.SessionEntity
import com.dabtracker.app.domain.model.Session
import kotlinx.coroutines.flow.Flow

interface SessionRepository {
    fun getAllSessions(): Flow<List<Session>>
    fun getSessionsByExtract(extractId: Long): Flow<List<Session>>
    fun getLastSession(): Flow<Session?>
    fun getRecentSessions(limit: Int): Flow<List<Session>>
    fun getSessionCount(): Flow<Int>
    fun getTotalDabWeight(): Flow<Double?>
    suspend fun getSessionById(id: Long): Session?
    suspend fun addSession(session: Session): Long
    suspend fun updateSession(session: Session)
    suspend fun deleteSession(id: Long)
    fun getRawEntities(): Flow<List<SessionEntity>>
    suspend fun insertAllRaw(entities: List<SessionEntity>)
    suspend fun deleteAll()
}
