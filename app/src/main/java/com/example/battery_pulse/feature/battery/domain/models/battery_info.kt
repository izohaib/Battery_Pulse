package com.example.battery_pulse.feature.battery.domain.models

class BatteryInfo(
    val percent: Int = 0,
    val isCharging: Boolean = false,
    val plugType: String = "Unplugged",
    val health: String = "Unknown",
    val temperatureCelsius: Float = 0f,
    val voltageMilliVolts: Int = 0,
    val currentMicroAmps: Int = 0,
    val chargingWatts: Float = 0f,
    val timeToFullMinutes: Int? = null
) {
}