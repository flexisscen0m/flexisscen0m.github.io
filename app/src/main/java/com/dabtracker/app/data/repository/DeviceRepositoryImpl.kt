package com.dabtracker.app.data.repository

import com.dabtracker.app.data.local.dao.DeviceDao
import com.dabtracker.app.data.local.entity.DeviceEntity
import com.dabtracker.app.domain.repository.DeviceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DeviceRepositoryImpl(private val dao: DeviceDao) : DeviceRepository {

    override fun getAllDevices(): Flow<List<String>> =
        dao.getAll().map { list -> list.map { it.name } }

    override suspend fun addDevice(name: String): Long =
        dao.insert(DeviceEntity(name = name))

    override suspend fun deleteDevice(id: Long) =
        dao.deleteById(id)

    override fun getRawEntities(): Flow<List<DeviceEntity>> = dao.getAll()

    override suspend fun insertAllRaw(entities: List<DeviceEntity>) = dao.insertAll(entities)
}
