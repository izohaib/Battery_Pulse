package com.example.battery_pulse.feature.battery.data.datasource

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.util.Log
import com.example.battery_pulse.feature.battery.domain.models.BatteryInfo
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import kotlin.math.abs

class BatteryDataSource @Inject constructor(private val context: Context) {

    private val _batteryInfo = MutableStateFlow(BatteryInfo())
    val batteryInfo: StateFlow<BatteryInfo> = _batteryInfo.asStateFlow()

    private var avgCurrentMa = 0.0

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(ctx: Context?, intent: Intent?) {
            if (intent?.action != Intent.ACTION_BATTERY_CHANGED) return
            _batteryInfo.value = parse(intent)
        }
    }

    fun register() {
        context.registerReceiver(receiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
    }

    fun unregister() {
        try { context.unregisterReceiver(receiver) } catch (e: Exception) { }
    }

    private fun parse(intent: Intent): BatteryInfo {
        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0)
        val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100)
        val percent = (level * 100) / scale

        val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING
                || status == BatteryManager.BATTERY_STATUS_FULL

        val plugType = when (intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)) {
            BatteryManager.BATTERY_PLUGGED_AC -> "AC"
            BatteryManager.BATTERY_PLUGGED_USB -> "USB"
            BatteryManager.BATTERY_PLUGGED_WIRELESS -> "Wireless"
            else -> "Unplugged"
        }

        val health = when (intent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1)) {
            BatteryManager.BATTERY_HEALTH_GOOD -> "Good"
            BatteryManager.BATTERY_HEALTH_OVERHEAT -> "Overheat"
            BatteryManager.BATTERY_HEALTH_DEAD -> "Dead"
            BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "Over Voltage"
            else -> "Unknown"
        }

        val temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) / 10f
        val voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0)

        val bm = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        val currentMicroAmps = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW)

// Store signed mA directly — positive=charging, negative=discharging
// Your device reports backwards (negative when charging), so flip it
//        val signedCurrentMa: Int = if (isCharging) abs(currentMicroAmps) else -abs(currentMicroAmps)

        val rawCurrentMa = if (isCharging) abs(currentMicroAmps) else -abs(currentMicroAmps)


// exponential moving average (stable)
        avgCurrentMa = if (avgCurrentMa == 0.0) {
            rawCurrentMa.toDouble()
        } else {
            (0.9 * avgCurrentMa) + (0.1 * rawCurrentMa)
        }

        val signedCurrentMa = avgCurrentMa.toInt()

        val volts = voltage / 1000f
        val watts = volts * (signedCurrentMa / 1000f)  // signed watts, V * A



        val timeToFull: Int? = if (isCharging && signedCurrentMa > 50) {
            val chargeCounter = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER)

            val fullCapUAh = if (chargeCounter > 0 && percent > 0) {
                (chargeCounter.toLong() * 100L) / percent.toLong()
            } else {
                4000000L
            }

            val remainingUAh = fullCapUAh * (100 - percent) / 100

            val currentUAhPerMinute = (signedCurrentMa * 1000.0) / 60.0

            val minutes = if (currentUAhPerMinute > 0) {
                (remainingUAh / currentUAhPerMinute).toInt()
            } else null

            if (minutes != null && minutes in 1..1440) minutes else null
        } else null

        return BatteryInfo(
            percent = percent,
            isCharging = isCharging,
            plugType = plugType,
            health = health,
            temperatureCelsius = temperature,
            voltageMilliVolts = voltage,
            currentMicroAmps = signedCurrentMa,
            chargingWatts = watts,
            timeToFullMinutes = timeToFull
        )
    }


}