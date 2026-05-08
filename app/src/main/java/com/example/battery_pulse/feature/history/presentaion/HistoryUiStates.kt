package com.example.battery_pulse.feature.history.presentaion

import com.example.battery_pulse.feature.history.domain.model.ChargingSession

sealed class HistoryUiState {
    object Loading : HistoryUiState()
    object Empty : HistoryUiState()
    data class Success(val sessions: List<ChargingSession>) : HistoryUiState()
    data class Error(val message: String) : HistoryUiState()
}