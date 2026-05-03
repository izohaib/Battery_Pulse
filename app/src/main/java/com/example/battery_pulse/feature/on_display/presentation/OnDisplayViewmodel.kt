package com.example.battery_pulse.feature.on_display.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.battery_pulse.feature.battery.domain.repository.BatteryRepository
import com.example.battery_pulse.feature.battery.domain.usecase.GetBatteryInfoUseCase
import com.example.battery_pulse.feature.battery.domain.models.BatteryInfo
import com.example.battery_pulse.feature.on_display.data.model.OnDisplaySettings
import com.example.battery_pulse.feature.on_display.domain.repository.OnDisplayRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnDisplayViewModel @Inject constructor(
    private val onDisplayRepository: OnDisplayRepository,
    private val getBatteryInfoUseCase: GetBatteryInfoUseCase,
    private val batteryRepository: BatteryRepository
) : ViewModel() {

    val settings: StateFlow<OnDisplaySettings?> = onDisplayRepository.settings
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    private val _batteryInfo = MutableStateFlow(BatteryInfo())
    val batteryInfo: StateFlow<BatteryInfo> = _batteryInfo.asStateFlow()

    init {
        batteryRepository.registerReceiver()
        observeBattery()
    }

    private fun observeBattery() {
        viewModelScope.launch {
            getBatteryInfoUseCase()
                .catch { }
                .collect { newBatteryInfo ->
                    _batteryInfo.value = newBatteryInfo
                }
        }
    }

    private fun update(newSettings: OnDisplaySettings) {
        viewModelScope.launch {
            onDisplayRepository.save(newSettings)
        }
    }

    fun setSpeedometerColor(newValue: Long) {
        val current = settings.value ?: return
        update(current.copy(speedometerColor = newValue))
    }

    fun setShowTemperature(newValue: Boolean) {
        val current = settings.value ?: return
        update(current.copy(showTemperature = newValue))
    }

    fun setShowVoltage(newValue: Boolean) {
        val current = settings.value ?: return
        update(current.copy(showVoltage = newValue))
    }

    fun setShowWattage(newValue: Boolean) {
        val current = settings.value ?: return
        update(current.copy(showWattage = newValue))
    }

    fun setShowCurrent(newValue: Boolean) {
        val current = settings.value ?: return
        update(current.copy(showCurrent = newValue))
    }

    fun setShowEstimatedTime(newValue: Boolean) {
        val current = settings.value ?: return
        update(current.copy(showEstimatedTime = newValue))
    }

    fun setDimAfterSeconds(newValue: Int) {
        val current = settings.value ?: return
        update(current.copy(dimAfterSeconds = newValue))
    }

    fun setHideAfterSeconds(newValue: Int) {
        val current = settings.value ?: return
        update(current.copy(hideAfterSeconds = newValue))
    }

    fun setAlwaysOnDisplay(newValue: Boolean) {
        val current = settings.value ?: return
        update(current.copy(alwaysOnDisplay = newValue))
    }

    fun setDateFormat(newValue: String) {
        val current = settings.value ?: return
        update(current.copy(dateFormat = newValue))
    }

    fun setHideClock(newValue: Boolean) {
        val current = settings.value ?: return
        update(current.copy(hideClock = newValue))
    }

    fun set24HourFormat(newValue: Boolean) {
        val current = settings.value ?: return
        update(current.copy(is24HourFormat = newValue))
    }

    fun showTorch(newValue: Boolean) {
        val current = settings.value ?: return
        update(current.copy(showTorch = newValue))
    }

    fun showCamera(newValue: Boolean) {
        val current = settings.value ?: return
        update(current.copy(showCamera = newValue))
    }
    override fun onCleared() {
        super.onCleared()
        batteryRepository.unRegisterReceiver()
    }
}