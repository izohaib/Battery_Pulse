package com.example.battery_pulse.feature.on_display.domain.repository

import com.example.battery_pulse.feature.on_display.data.model.OnDisplaySettings
import kotlinx.coroutines.flow.Flow

interface OnDisplayRepository {
    val settings: Flow<OnDisplaySettings>
    suspend fun save(settings: OnDisplaySettings)
}