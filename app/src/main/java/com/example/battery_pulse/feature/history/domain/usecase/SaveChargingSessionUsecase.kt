package com.example.battery_pulse.feature.history.domain.usecase

import com.example.battery_pulse.feature.history.domain.repository.ChargingHistoryRepository
import javax.inject.Inject

class SaveChargingSessionUseCase @Inject constructor(
    private val repository: ChargingHistoryRepository
) {
    suspend fun start(startTime: Long, startBattery: Int) =
        repository.startSession(startTime, startBattery)

    suspend fun end(endTime: Long, endBattery: Int) =
        repository.endSession(endTime, endBattery)

    suspend fun hasOpenSession(): Boolean {
        return repository.hasOpenSession()
    }

    suspend fun deleteSession(id: Int){
        return repository.deleteSession(id)
    }
}