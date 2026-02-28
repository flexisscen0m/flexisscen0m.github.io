package com.dabtracker.app.data.export

import com.dabtracker.app.data.local.entity.DeviceEntity
import com.dabtracker.app.data.local.entity.ExtractEntity
import com.dabtracker.app.data.local.entity.SessionEntity
import com.dabtracker.app.domain.repository.DeviceRepository
import com.dabtracker.app.domain.repository.ExtractRepository
import com.dabtracker.app.domain.repository.SessionRepository
import kotlinx.coroutines.flow.first
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class ExportData(
    val version: Int = 1,
    val exportedAt: Long = System.currentTimeMillis(),
    val extracts: List<ExportExtract>,
    val sessions: List<ExportSession>,
    val devices: List<ExportDevice>
)

@Serializable
data class ExportExtract(
    val id: Long,
    val name: String,
    val category: String,
    val strainName: String,
    val initialWeightGrams: Double,
    val purchaseDate: Long? = null,
    val notes: String? = null,
    val createdAt: Long
)

@Serializable
data class ExportSession(
    val id: Long,
    val extractId: Long,
    val dabWeightGrams: Double,
    val temperatureCelsius: Int,
    val deviceName: String,
    val timestamp: Long,
    val flavorRating: Int? = null,
    val vaporQualityRating: Int? = null,
    val effectIntensityRating: Int? = null,
    val effectDurationRating: Int? = null,
    val overallRating: Int? = null,
    val notes: String? = null
)

@Serializable
data class ExportDevice(
    val id: Long,
    val name: String
)

class ExportImportManager(
    private val extractRepository: ExtractRepository,
    private val sessionRepository: SessionRepository,
    private val deviceRepository: DeviceRepository
) {
    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    suspend fun exportToJson(): String {
        val extracts = extractRepository.getRawEntities().first()
        val sessions = sessionRepository.getRawEntities().first()
        val devices = deviceRepository.getRawEntities().first()

        val exportData = ExportData(
            extracts = extracts.map { it.toExport() },
            sessions = sessions.map { it.toExport() },
            devices = devices.map { it.toExport() }
        )

        return json.encodeToString(ExportData.serializer(), exportData)
    }

    suspend fun importFromJson(jsonString: String) {
        val data = json.decodeFromString(ExportData.serializer(), jsonString)

        sessionRepository.deleteAll()

        extractRepository.insertAllRaw(data.extracts.map { it.toEntity() })
        sessionRepository.insertAllRaw(data.sessions.map { it.toEntity() })
        deviceRepository.insertAllRaw(data.devices.map { it.toEntity() })
    }
}

private fun ExtractEntity.toExport() = ExportExtract(
    id = id, name = name, category = category, strainName = strainName,
    initialWeightGrams = initialWeightGrams, purchaseDate = purchaseDate,
    notes = notes, createdAt = createdAt
)

private fun ExportExtract.toEntity() = ExtractEntity(
    id = id, name = name, category = category, strainName = strainName,
    initialWeightGrams = initialWeightGrams, purchaseDate = purchaseDate,
    notes = notes, createdAt = createdAt
)

private fun SessionEntity.toExport() = ExportSession(
    id = id, extractId = extractId, dabWeightGrams = dabWeightGrams,
    temperatureCelsius = temperatureCelsius, deviceName = deviceName,
    timestamp = timestamp, flavorRating = flavorRating,
    vaporQualityRating = vaporQualityRating, effectIntensityRating = effectIntensityRating,
    effectDurationRating = effectDurationRating, overallRating = overallRating, notes = notes
)

private fun ExportSession.toEntity() = SessionEntity(
    id = id, extractId = extractId, dabWeightGrams = dabWeightGrams,
    temperatureCelsius = temperatureCelsius, deviceName = deviceName,
    timestamp = timestamp, flavorRating = flavorRating,
    vaporQualityRating = vaporQualityRating, effectIntensityRating = effectIntensityRating,
    effectDurationRating = effectDurationRating, overallRating = overallRating, notes = notes
)

private fun DeviceEntity.toExport() = ExportDevice(id = id, name = name)
private fun ExportDevice.toEntity() = DeviceEntity(id = id, name = name)
