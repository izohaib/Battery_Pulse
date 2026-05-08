package com.example.battery_pulse.feature.history.data.repository

import com.example.battery_pulse.feature.history.data.local.dao.ChargingSessionDao
import com.example.battery_pulse.feature.history.data.local.entity.ChargingSessionEntity
import com.example.battery_pulse.feature.history.domain.model.ChargingSession
import com.example.battery_pulse.feature.history.domain.repository.ChargingHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChargingHistoryRepositoryImpl @Inject constructor(
    private val dao: ChargingSessionDao
) : ChargingHistoryRepository {

    override fun getAllSessions(): Flow<List<ChargingSession>> {
        return dao.getAllSessions().map { list ->
            list.mapNotNull { entity ->
                // skip incomplete sessions just in case
                if (entity.endTime == null || entity.endBattery == null) return@mapNotNull null
                ChargingSession(
                    id = entity.id,
                    startTime = entity.startTime,
                    endTime = entity.endTime,
                    startBattery = entity.startBattery,
                    endBattery = entity.endBattery,
                    gainedPercent = entity.endBattery - entity.startBattery,
                    durationMinutes = (entity.endTime - entity.startTime) / 60000
                )
            }
        }
    }

    override suspend fun startSession(startTime: Long, startBattery: Int) {
        dao.insertSession(
            ChargingSessionEntity(
                startTime = startTime,
                endTime = null,
                startBattery = startBattery,
                endBattery = null
            )
        )
    }

    override suspend fun endSession(endTime: Long, endBattery: Int) {
        dao.closeLatestSession(endTime, endBattery)
    }

    override suspend fun hasOpenSession(): Boolean {
        return dao.getOpenSession() != null
    }

    override suspend fun deleteSession(id: Int) {
      return dao.deleteSessionById(id)
    }
}
