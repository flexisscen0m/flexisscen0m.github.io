package com.dabtracker.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.dabtracker.app.data.local.entity.ExtractEntity
import kotlinx.coroutines.flow.Flow

data class ExtractWithRemaining(
    val id: Long,
    val name: String,
    val category: String,
    val strainName: String,
    val initialWeightGrams: Double,
    val remainingWeightGrams: Double,
    val purchaseDate: Long?,
    val notes: String?,
    val createdAt: Long
)

@Dao
interface ExtractDao {

    @Query("""
        SELECT e.id, e.name, e.category, e.strainName, e.initialWeightGrams,
               e.initialWeightGrams - COALESCE(SUM(s.dabWeightGrams), 0) as remainingWeightGrams,
               e.purchaseDate, e.notes, e.createdAt
        FROM extracts e
        LEFT JOIN sessions s ON e.id = s.extractId
        GROUP BY e.id
        ORDER BY e.name ASC
    """)
    fun getAllWithRemaining(): Flow<List<ExtractWithRemaining>>

    @Query("""
        SELECT e.id, e.name, e.category, e.strainName, e.initialWeightGrams,
               e.initialWeightGrams - COALESCE(SUM(s.dabWeightGrams), 0) as remainingWeightGrams,
               e.purchaseDate, e.notes, e.createdAt
        FROM extracts e
        LEFT JOIN sessions s ON e.id = s.extractId
        WHERE e.id = :id
        GROUP BY e.id
    """)
    fun getByIdWithRemaining(id: Long): Flow<ExtractWithRemaining?>

    @Query("""
        SELECT e.id, e.name, e.category, e.strainName, e.initialWeightGrams,
               e.initialWeightGrams - COALESCE(SUM(s.dabWeightGrams), 0) as remainingWeightGrams,
               e.purchaseDate, e.notes, e.createdAt
        FROM extracts e
        LEFT JOIN sessions s ON e.id = s.extractId
        WHERE e.category = :category
        GROUP BY e.id
        ORDER BY e.name ASC
    """)
    fun getByCategory(category: String): Flow<List<ExtractWithRemaining>>

    @Query("SELECT * FROM extracts WHERE id = :id")
    suspend fun getById(id: Long): ExtractEntity?

    @Query("SELECT * FROM extracts ORDER BY name ASC")
    fun getAll(): Flow<List<ExtractEntity>>

    @Query("SELECT DISTINCT category FROM extracts ORDER BY category ASC")
    fun getAllCategories(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(extract: ExtractEntity): Long

    @Update
    suspend fun update(extract: ExtractEntity)

    @Delete
    suspend fun delete(extract: ExtractEntity)

    @Query("DELETE FROM extracts WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(extracts: List<ExtractEntity>)
}
