package com.example.battery_pulse.feature.on_display.data.model

data class OnDisplaySettings(
    val speedometerColor: Long = 0xFF00E676,
    val showTemperature: Boolean = true,
    val showVoltage: Boolean = true,
    val showWattage: Boolean = true,
    val showCurrent: Boolean = true,
    val showEstimatedTime: Boolean = true,
    val dimAfterSeconds: Int = 10,
    val hideAfterSeconds: Int = 15,
    val dateFormat: String = "EEE, dd MMM",
    val hideClock: Boolean = false,
    val is24HourFormat: Boolean = false,
    val alwaysOnDisplay: Boolean = false,
    val showTorch: Boolean = true,
    val showCamera: Boolean = true,
)