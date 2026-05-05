package com.example.battery_pulse.feature.app_usage.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.battery_pulse.feature.app_usage.domain.models.AppUsageStat
import com.example.battery_pulse.feature.app_usage.domain.usecase.GetAppUsageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@HiltViewModel
class AppUsageViewModel @Inject constructor(
    private val getAppUsageUseCase: GetAppUsageUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<AppUsageUiState>(AppUsageUiState.Loading)
    val uiState: StateFlow<AppUsageUiState> = _uiState.asStateFlow()

    init {
        recheckPermission()
    }

    fun recheckPermission() {
        if (getAppUsageUseCase.hasPermission()) {
            load()
        } else {
            _uiState.value = AppUsageUiState.NoPermission
        }
    }

    fun load() {
        viewModelScope.launch {
            _uiState.value = AppUsageUiState.Loading
            try {
                val data = getAppUsageUseCase()

                _uiState.value = when{
                    data.isEmpty() -> AppUsageUiState.Empty
                    else -> AppUsageUiState.Success(data)
                }


            } catch (e: Exception) {
                _uiState.value = AppUsageUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}

