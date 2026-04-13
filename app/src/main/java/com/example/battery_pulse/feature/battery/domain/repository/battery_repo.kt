package com.example.battery_pulse.feature.battery.domain.repository

import com.example.battery_pulse.core.Resource
import com.example.battery_pulse.feature.battery.domain.models.BatteryInfo
import kotlinx.coroutines.flow.Flow

interface BatteryRepository {
    fun getBatteryInfo(): Flow<BatteryInfo>

    fun unRegisterReceiver()

    fun registerReceiver()
}