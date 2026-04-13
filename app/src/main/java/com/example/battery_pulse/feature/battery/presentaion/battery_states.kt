package com.example.battery_pulse.feature.battery.presentaion

import com.example.battery_pulse.feature.battery.domain.models.BatteryInfo

sealed class BatteryUiState {
    object Loading : BatteryUiState()
    data class Success(val data: BatteryInfo) : BatteryUiState()
    data class Error(val message: String) : BatteryUiState()
}