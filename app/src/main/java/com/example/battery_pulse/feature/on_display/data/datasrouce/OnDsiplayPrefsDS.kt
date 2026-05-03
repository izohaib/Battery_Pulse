package com.example.battery_pulse.feature.on_display.data.datasrouce

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.battery_pulse.feature.on_display.data.model.OnDisplaySettings

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.onDisplayDataStore by preferencesDataStore("on_display_settings")

class OnDisplaySettingsDataSource(private val context: Context) {

    companion object {
        val SPEEDOMETER_COLOR = longPreferencesKey("speedometer_color")
        val SHOW_TEMPERATURE = booleanPreferencesKey("show_temperature")
        val SHOW_VOLTAGE = booleanPreferencesKey("show_voltage")
        val SHOW_WATTAGE = booleanPreferencesKey("show_wattage")
        val SHOW_CURRENT = booleanPreferencesKey("show_current")
        val SHOW_ESTIMATED_TIME = booleanPreferencesKey("show_estimated_time")
        val DIM_AFTER_SECONDS = intPreferencesKey("dim_after_seconds")
        val HIDE_AFTER_SECONDS = intPreferencesKey("hide_after_seconds")
        val DATE_FORMAT = stringPreferencesKey("date_format")
        val HIDE_CLOCK = booleanPreferencesKey("hide_clock")
        val USE_24_HOUR_FORMAT = booleanPreferencesKey("use_24_hour_format")
        val ALWAYS_ON_DISPLAY = booleanPreferencesKey("always_on_Display")
        val SHOW_CAMERA  = booleanPreferencesKey("show_camera")
        val SHOW_TORCH  = booleanPreferencesKey("show_torch")
    }

    // returning data in flow
    val settings: Flow<OnDisplaySettings> = context.onDisplayDataStore.data.map { prefs ->
        OnDisplaySettings(
            speedometerColor = prefs[SPEEDOMETER_COLOR] ?: 0xFF00E676,
            showTemperature = prefs[SHOW_TEMPERATURE] ?: true,
            showVoltage = prefs[SHOW_VOLTAGE] ?: true,
            showWattage = prefs[SHOW_WATTAGE] ?: true,
            showCurrent = prefs[SHOW_CURRENT] ?: true,
            showEstimatedTime = prefs[SHOW_ESTIMATED_TIME] ?: true,
            dimAfterSeconds = prefs[DIM_AFTER_SECONDS] ?: 10,
            hideAfterSeconds = prefs[HIDE_AFTER_SECONDS] ?: 15,
            dateFormat = prefs[DATE_FORMAT] ?: "EEE, dd MMM",
            hideClock = prefs[HIDE_CLOCK] ?: false,
            is24HourFormat = prefs[USE_24_HOUR_FORMAT] ?: false,
            alwaysOnDisplay = prefs[ALWAYS_ON_DISPLAY] ?: false,
            showCamera = prefs[SHOW_CAMERA] ?: true,
            showTorch = prefs[SHOW_TORCH] ?: true,
        )
    }

    suspend fun save(settings: OnDisplaySettings) {
        context.onDisplayDataStore.edit { prefs ->
            prefs[SPEEDOMETER_COLOR] = settings.speedometerColor
            prefs[SHOW_TEMPERATURE] = settings.showTemperature
            prefs[SHOW_VOLTAGE] = settings.showVoltage
            prefs[SHOW_WATTAGE] = settings.showWattage
            prefs[SHOW_CURRENT] = settings.showCurrent
            prefs[SHOW_ESTIMATED_TIME] = settings.showEstimatedTime
            prefs[DIM_AFTER_SECONDS] = settings.dimAfterSeconds
            prefs[HIDE_AFTER_SECONDS] = settings.hideAfterSeconds
            prefs[DATE_FORMAT] = settings.dateFormat
            prefs[HIDE_CLOCK] = settings.hideClock
            prefs[USE_24_HOUR_FORMAT] = settings.is24HourFormat
            prefs[ALWAYS_ON_DISPLAY] = settings.alwaysOnDisplay
            prefs[SHOW_CAMERA] = settings.showCamera
            prefs[SHOW_TORCH] = settings.showTorch
        }
    }
}