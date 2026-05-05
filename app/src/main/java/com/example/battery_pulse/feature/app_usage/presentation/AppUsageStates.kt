package com.example.battery_pulse.feature.app_usage.presentation

import com.example.battery_pulse.feature.app_usage.domain.models.AppUsageStat

sealed class AppUsageUiState {
    object NoPermission : AppUsageUiState()
    object Loading : AppUsageUiState()
    object Empty : AppUsageUiState()
    data class Success(val apps: List<AppUsageStat>) : AppUsageUiState()
    data class Error(val message: String) : AppUsageUiState()
}