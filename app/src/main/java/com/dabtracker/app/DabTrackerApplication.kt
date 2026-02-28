package com.dabtracker.app

import android.app.Application
import com.dabtracker.app.data.export.ExportImportManager
import com.dabtracker.app.data.local.AppDatabase
import com.dabtracker.app.data.local.CustomCategoryManager
import com.dabtracker.app.data.repository.DeviceRepositoryImpl
import com.dabtracker.app.data.repository.ExtractRepositoryImpl
import com.dabtracker.app.data.repository.SessionRepositoryImpl
import com.dabtracker.app.domain.repository.DeviceRepository
import com.dabtracker.app.domain.repository.ExtractRepository
import com.dabtracker.app.domain.repository.SessionRepository

class DabTrackerApplication : Application() {

    private val database by lazy { AppDatabase.getInstance(this) }

    val extractRepository: ExtractRepository by lazy {
        ExtractRepositoryImpl(database.extractDao())
    }

    val sessionRepository: SessionRepository by lazy {
        SessionRepositoryImpl(database.sessionDao())
    }

    val deviceRepository: DeviceRepository by lazy {
        DeviceRepositoryImpl(database.deviceDao())
    }

    val customCategoryManager: CustomCategoryManager by lazy {
        CustomCategoryManager(this)
    }

    val exportImportManager: ExportImportManager by lazy {
        ExportImportManager(extractRepository, sessionRepository, deviceRepository)
    }
}
