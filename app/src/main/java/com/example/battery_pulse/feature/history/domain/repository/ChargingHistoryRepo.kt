package com.example.battery_pulse.feature.history.domain.repository

import com.example.battery_pulse.feature.history.domain.model.ChargingSession
import kotlinx.coroutines.flow.Flow

interface ChargingHistoryRepository {
    fun getAllSessions(): Flow<List<ChargingSession>>
    suspend fun startSession(startTime: Long, startBattery: Int)
    suspend fun endSession(endTime: Long, endBattery: Int)
    suspend fun hasOpenSession(): Boolean
    suspend fun deleteSession(id: Int)
}