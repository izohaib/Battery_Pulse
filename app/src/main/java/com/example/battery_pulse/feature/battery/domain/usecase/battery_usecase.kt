package com.example.battery_pulse.feature.battery.domain.usecase

import com.example.battery_pulse.feature.battery.domain.models.BatteryInfo
import com.example.battery_pulse.feature.battery.domain.repository.BatteryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBatteryInfoUseCase @Inject constructor(
    private val repository: BatteryRepository
) {
    operator fun invoke(): Flow<BatteryInfo> = repository.getBatteryInfo()
}