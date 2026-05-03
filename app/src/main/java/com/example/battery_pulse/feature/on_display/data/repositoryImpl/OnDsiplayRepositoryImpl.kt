package com.example.battery_pulse.feature.on_display.data.repositoryImpl

import com.example.battery_pulse.feature.on_display.data.model.OnDisplaySettings
import com.example.battery_pulse.feature.on_display.data.datasrouce.OnDisplaySettingsDataSource
import com.example.battery_pulse.feature.on_display.domain.repository.OnDisplayRepository
import kotlinx.coroutines.flow.Flow

class OnDisplayRepositoryImpl(private val dataSource: OnDisplaySettingsDataSource) : OnDisplayRepository{

    override val settings: Flow<OnDisplaySettings> = dataSource.settings

    override suspend fun save(settings: OnDisplaySettings) {
       dataSource.save(settings = settings)
    }
}