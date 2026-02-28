package com.dabtracker.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dabtracker.app.data.local.dao.DeviceDao
import com.dabtracker.app.data.local.dao.ExtractDao
import com.dabtracker.app.data.local.dao.SessionDao
import com.dabtracker.app.data.local.entity.DeviceEntity
import com.dabtracker.app.data.local.entity.ExtractEntity
import com.dabtracker.app.data.local.entity.SessionEntity

@Database(
    entities = [ExtractEntity::class, SessionEntity::class, DeviceEntity::class],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun extractDao(): ExtractDao
    abstract fun sessionDao(): SessionDao
    abstract fun deviceDao(): DeviceDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "dab_tracker.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
