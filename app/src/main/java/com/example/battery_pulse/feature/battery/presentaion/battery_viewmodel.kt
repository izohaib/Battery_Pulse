package com.example.battery_pulse.feature.battery.presentaion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.battery_pulse.feature.battery.data.repositoryImpl.BatteryRepositoryImpl
import com.example.battery_pulse.feature.battery.domain.repository.BatteryRepository
import com.example.battery_pulse.feature.battery.domain.usecase.GetBatteryInfoUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class BatteryViewModel(
    private val getBatteryInfoUseCase: GetBatteryInfoUseCase,
    private val repository: BatteryRepository
) : ViewModel() {

    private val _state = MutableStateFlow<BatteryUiState>(BatteryUiState.Loading)
    val state: StateFlow<BatteryUiState> = _state.asStateFlow()

    init {
        repository.registerReceiver()
        observeBattery()
    }

    private fun observeBattery() {
        viewModelScope.launch {
            try {
                getBatteryInfoUseCase()
                    .onStart {
                        _state.value = BatteryUiState.Loading
                    }
                    .catch {
                        _state.value = BatteryUiState.Error(it.localizedMessage ?: "Unknown error")
                    }
                    .collect { info ->
                        _state.value = BatteryUiState.Success(info)
                    }

            } catch (e: Exception) {
                _state.value = BatteryUiState.Error(e.localizedMessage ?: "Unknown error")
            }
        }
    }

    fun startListening() {
        repository.registerReceiver()
    }

    override fun onCleared() {
        super.onCleared()
        repository.unRegisterReceiver()
    }
}