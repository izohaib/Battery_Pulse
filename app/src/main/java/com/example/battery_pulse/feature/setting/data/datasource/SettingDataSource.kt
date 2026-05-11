package com.example.battery_pulse.feature.setting.data.datasource

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.battery_pulse.feature.setting.domain.model.SettingsEntity
import com.example.battery_pulse.feature.setting.domain.model.TemperatureUnit
import com.example.battery_pulse.feature.setting.domain.model.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.settingsDataStore by preferencesDataStore("app_settings")

class SettingsDataSource @Inject constructor(private val context: Context) {

    companion object {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val CHARGE_LIMIT_ENABLED = booleanPreferencesKey("charge_limit_enabled")
        val CHARGE_LIMIT_PERCENT = intPreferencesKey("charge_limit_percent")
        val DISCHARGE_LIMIT_ENABLED = booleanPreferencesKey("discharge_limit_enabled")
        val DISCHARGE_LIMIT_PERCENT = intPreferencesKey("discharge_limit_percent")
        val TEMPERATURE_UNIT = stringPreferencesKey("temperature_unit")
    }

    val settings: Flow<SettingsEntity> = context.settingsDataStore.data.map { prefs ->
        SettingsEntity(
            themeMode = ThemeMode.valueOf(prefs[THEME_MODE] ?: ThemeMode.SYSTEM.name),
            chargeLimitEnabled = prefs[CHARGE_LIMIT_ENABLED] ?: true,
            chargeLimitPercent = prefs[CHARGE_LIMIT_PERCENT] ?: 90,
            dischargeLimitEnabled = prefs[DISCHARGE_LIMIT_ENABLED] ?: true,
            dischargeLimitPercent = prefs[DISCHARGE_LIMIT_PERCENT] ?: 20,
            temperatureUnit = TemperatureUnit.valueOf(prefs[TEMPERATURE_UNIT] ?: TemperatureUnit.CELSIUS.name)
        )
    }

    suspend fun save(settings: SettingsEntity) {
        context.settingsDataStore.edit { prefs ->
            prefs[THEME_MODE] = settings.themeMode.name
            prefs[CHARGE_LIMIT_ENABLED] = settings.chargeLimitEnabled
            prefs[CHARGE_LIMIT_PERCENT] = settings.chargeLimitPercent
            prefs[DISCHARGE_LIMIT_ENABLED] = settings.dischargeLimitEnabled
            prefs[DISCHARGE_LIMIT_PERCENT] = settings.dischargeLimitPercent
            prefs[TEMPERATURE_UNIT] = settings.temperatureUnit.name
        }
    }
}