package com.example.battery_pulse.feature.history.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.battery_pulse.feature.history.data.local.dao.ChargingSessionDao
import com.example.battery_pulse.feature.history.data.local.entity.ChargingSessionEntity

@Database(
    entities = [ChargingSessionEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun chargingSessionDao(): ChargingSessionDao
}