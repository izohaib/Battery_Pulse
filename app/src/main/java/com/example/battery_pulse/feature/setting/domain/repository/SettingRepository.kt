package com.example.battery_pulse.feature.setting.domain.repository


import com.example.battery_pulse.feature.setting.domain.model.SettingsEntity
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun getSettings(): Flow<SettingsEntity>
    suspend fun saveSettings(settings: SettingsEntity)
}