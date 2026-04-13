package com.example.battery_pulse.feature.battery.data.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat.startForegroundService

class PowerReceiver : BroadcastReceiver() {


    override fun onReceive(context: Context, intent: Intent?) {
        // tells us current system event
        val action = intent?.action

        when (action) {
            Intent.ACTION_POWER_CONNECTED -> {
                startForegroundService(
                    context,
                    Intent(context, BatteryService::class.java)
                )
                Log.d("Starting Forground", "Forground service started")
            }

            Intent.ACTION_POWER_DISCONNECTED -> {
                Log.d("Stop Forground", "Forground service Stopped")
                context.stopService(Intent(context, BatteryService::class.java))
                Log.d("Stop Forground", "Forground service Stopped")
            }
        }


    }
}