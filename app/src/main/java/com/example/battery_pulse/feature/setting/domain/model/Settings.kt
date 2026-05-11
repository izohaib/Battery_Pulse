package com.example.battery_pulse.feature.setting.domain.model

data class SettingsEntity(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val chargeLimitEnabled: Boolean = true,
    val chargeLimitPercent: Int = 90,
    val dischargeLimitEnabled: Boolean = true,
    val dischargeLimitPercent: Int = 20,
    val temperatureUnit: TemperatureUnit = TemperatureUnit.CELSIUS
)

enum class ThemeMode { LIGHT, DARK, SYSTEM }
enum class TemperatureUnit { CELSIUS, FAHRENHEIT }