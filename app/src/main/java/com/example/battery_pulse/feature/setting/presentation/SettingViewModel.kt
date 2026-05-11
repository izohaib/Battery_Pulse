package com.example.battery_pulse.feature.setting.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.battery_pulse.feature.setting.domain.model.SettingsEntity
import com.example.battery_pulse.feature.setting.domain.model.TemperatureUnit
import com.example.battery_pulse.feature.setting.domain.model.ThemeMode
import com.example.battery_pulse.feature.setting.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    val settingsRepository: SettingsRepository
) : ViewModel() {
    val settings: StateFlow<SettingsEntity?> = settingsRepository.getSettings()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )


    fun update(newSettings: SettingsEntity) {
        viewModelScope.launch {
            settingsRepository.saveSettings(newSettings)
        }
    }

    fun setTheme(mode: ThemeMode) {
        val current = settings.value ?: return
        update(current.copy(themeMode = mode))
    }

    fun setChargeLimitEnabled(enabled: Boolean) {
        val current = settings.value ?: return
        update(current.copy(chargeLimitEnabled = enabled))
    }

    fun setChargeLimitPercent(percent: Int) {
        val current = settings.value ?: return
        update(current.copy(chargeLimitPercent = percent))
    }

    fun setDischargeLimitEnabled(enabled: Boolean) {
        val current = settings.value ?: return
        update(current.copy(dischargeLimitEnabled = enabled))
    }

    fun setDischargeLimitPercent(percent: Int) {
        val current = settings.value ?: return
        update(current.copy(dischargeLimitPercent = percent))
    }

    fun setTemperatureUnit(unit: TemperatureUnit) {
        val current = settings.value ?: return
        update(current.copy(temperatureUnit = unit))
    }
}