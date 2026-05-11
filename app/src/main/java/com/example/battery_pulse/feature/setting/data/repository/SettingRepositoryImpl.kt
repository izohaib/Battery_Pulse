package com.example.battery_pulse.feature.setting.data.repository

import com.example.battery_pulse.feature.setting.data.datasource.SettingsDataSource
import com.example.battery_pulse.feature.setting.domain.model.SettingsEntity
import com.example.battery_pulse.feature.setting.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val dataSource: SettingsDataSource
) : SettingsRepository {

    override fun getSettings(): Flow<SettingsEntity> = dataSource.settings

    override suspend fun saveSettings(settings: SettingsEntity) {
        dataSource.save(settings)
    }
}