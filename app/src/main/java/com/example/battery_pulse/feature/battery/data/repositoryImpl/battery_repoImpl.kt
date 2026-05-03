package com.example.battery_pulse.feature.battery.data.repositoryImpl

import com.example.battery_pulse.feature.battery.data.datasource.BatteryDataSource
import com.example.battery_pulse.feature.battery.domain.models.BatteryInfo
import com.example.battery_pulse.feature.battery.domain.repository.BatteryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class BatteryRepositoryImpl @Inject constructor(
    private val dataSource: BatteryDataSource
) : BatteryRepository {

    override fun getBatteryInfo(): Flow<BatteryInfo> {
        try {
            val info: Flow<BatteryInfo> = dataSource.batteryInfo
            return info
        } catch (e: Exception) {
            throw e
        }
    }

    override fun unRegisterReceiver() {
        dataSource.unregister()
    }

    override fun registerReceiver() {
        dataSource.register()
    }
}
