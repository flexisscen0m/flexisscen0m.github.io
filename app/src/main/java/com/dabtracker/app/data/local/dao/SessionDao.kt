package com.dabtracker.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.dabtracker.app.data.local.entity.SessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {

    @Query("SELECT * FROM sessions ORDER BY timestamp DESC")
    fun getAll(): Flow<List<SessionEntity>>

    @Query("SELECT * FROM sessions WHERE extractId = :extractId ORDER BY timestamp DESC")
    fun getByExtractId(extractId: Long): Flow<List<SessionEntity>>

    @Query("SELECT * FROM sessions WHERE id = :id")
    suspend fun getById(id: Long): SessionEntity?

    @Query("SELECT * FROM sessions ORDER BY timestamp DESC LIMIT 1")
    fun getLastSession(): Flow<SessionEntity?>

    @Query("SELECT * FROM sessions ORDER BY timestamp DESC LIMIT :limit")
    fun getRecent(limit: Int): Flow<List<SessionEntity>>

    @Query("SELECT COUNT(*) FROM sessions")
    fun getSessionCount(): Flow<Int>

    @Query("SELECT SUM(dabWeightGrams) FROM sessions")
    fun getTotalDabWeight(): Flow<Double?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(session: SessionEntity): Long

    @Update
    suspend fun update(session: SessionEntity)

    @Delete
    suspend fun delete(session: SessionEntity)

    @Query("DELETE FROM sessions WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(sessions: List<SessionEntity>)

    @Query("DELETE FROM sessions")
    suspend fun deleteAll()
}
