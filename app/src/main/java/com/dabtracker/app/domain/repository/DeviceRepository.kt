package com.dabtracker.app.domain.repository

import com.dabtracker.app.data.local.entity.DeviceEntity
import kotlinx.coroutines.flow.Flow

interface DeviceRepository {
    fun getAllDevices(): Flow<List<String>>
    suspend fun addDevice(name: String): Long
    suspend fun deleteDevice(id: Long)
    fun getRawEntities(): Flow<List<DeviceEntity>>
    suspend fun insertAllRaw(entities: List<DeviceEntity>)
}
