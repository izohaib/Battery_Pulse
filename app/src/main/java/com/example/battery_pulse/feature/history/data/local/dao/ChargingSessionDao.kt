package com.example.battery_pulse.feature.history.data.local.dao

import androidx.room.*
import com.example.battery_pulse.feature.history.data.local.entity.ChargingSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChargingSessionDao {

    @Insert
    suspend fun insertSession(session: ChargingSessionEntity): Long

    // update endTime and endBattery of the last open session
    @Query("""
        UPDATE charging_sessions 
        SET endTime = :endTime, endBattery = :endBattery 
        WHERE id = (SELECT MAX(id) FROM charging_sessions WHERE endTime IS NULL)
    """)
    suspend fun closeLatestSession(endTime: Long, endBattery: Int)

    // get all completed sessions, newest first
    @Query("SELECT * FROM charging_sessions WHERE endTime IS NOT NULL ORDER BY startTime DESC")
    fun getAllSessions(): Flow<List<ChargingSessionEntity>>

    // check if there is already an open session (plugged in but not yet unplugged)
    @Query("SELECT * FROM charging_sessions WHERE endTime IS NULL LIMIT 1")
    suspend fun getOpenSession(): ChargingSessionEntity?

    @Query("DELETE FROM charging_sessions WHERE id = :id")
    suspend fun deleteSessionById(id: Int)
}