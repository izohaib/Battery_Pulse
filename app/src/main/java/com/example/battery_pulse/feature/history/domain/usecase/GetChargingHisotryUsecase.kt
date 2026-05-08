package com.example.battery_pulse.feature.history.domain.usecase

import com.example.battery_pulse.feature.history.domain.model.ChargingSession
import com.example.battery_pulse.feature.history.domain.repository.ChargingHistoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetChargingHistoryUseCase @Inject constructor(
    private val repository: ChargingHistoryRepository
) {
    operator fun invoke(): Flow<List<ChargingSession>> = repository.getAllSessions()
}