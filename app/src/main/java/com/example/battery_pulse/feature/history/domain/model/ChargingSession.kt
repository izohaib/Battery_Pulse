package com.example.battery_pulse.feature.history.domain.model

data class ChargingSession(
    val id: Int,
    val startTime: Long,
    val endTime: Long,
    val startBattery: Int,
    val endBattery: Int,
    val gainedPercent: Int,        // endBattery - startBattery
    val durationMinutes: Long      // (endTime - startTime) / 60000
)