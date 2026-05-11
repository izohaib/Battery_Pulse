package com.example.battery_pulse.feature.on_display.presentation.fullScreenIntentActivity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.battery_pulse.core.utils.TorchHelper
import com.example.battery_pulse.feature.on_display.presentation.OnDisplayViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChargingDisplayActivity : ComponentActivity() {

    private val viewModel: OnDisplayViewModel by viewModels()


    // these are Compose states — when they change, UI recomposes automatically
//    private var percent by mutableIntStateOf(0)

    private val receiver = object : BroadcastReceiver() {
        overri
        de fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                // live percent update from BatteryService
//                "ACTION_CHARGING_UPDATE" -> {
//                    percent = intent.getIntExtra("percent", percent)
//                }
                // charger removed → close this activity
                "ACTION_DISMISS_CHARGING_DISPLAY" -> {
                    finish()
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O_MR1)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        TorchHelper.register(this)

        // get initial percent passed from notification intent
//        percent = intent.getIntExtra("percent", 0)

        // --- Lockscreen + Screen ON flags ---
        // These tell Android: show this activity over lockscreen + wake screen
        setShowWhenLocked(true)   // shows over lockscreen (API 27+)
        setTurnScreenOn(true)     // wakes screen when activity launches (API 27+)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        // keep screen on while charging display is visible
//        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
//

        // make content go edge to edge (no system bar padding)
//        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {

            val settings by viewModel.settings.collectAsStateWithLifecycle()
            val batteryInfo by viewModel.batteryInfo.collectAsStateWithLifecycle()
            val s = settings ?: return@setContent

            ChargingDisplayScreen(settings = s, batteryInfo = batteryInfo)
        }
    }

//    override fun onNewIntent(intent: Intent) {
//        super.onNewIntent(intent)
//        // called if activity is already open and notification fires again
//        percent = intent.getIntExtra("percent", percent)
//    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onResume() {
        super.onResume()
        // register receiver only while activity is visible
        val filter = IntentFilter().apply {
            addAction("ACTION_CHARGING_UPDATE")
            addAction("ACTION_DISMISS_CHARGING_DISPLAY")
        }
        registerReceiver(receiver, filter, RECEIVER_NOT_EXPORTED)
    }

    override fun onPause() {
        super.onPause()
        // unregister when activity goes to background
        unregisterReceiver(receiver)
    }

    override fun onDestroy() {
        super.onDestroy()
        TorchHelper.turnOff(this)
        TorchHelper.unregister(this)
    }

    fun restoreBrightness() {
        //turn keep screen on display again
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        //restore the brightness param
        val params = window.attributes
        params.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
        window.attributes = params
        Log.d("DimHide", "hideScreen: FLAG_KEEP_SCREEN_ON cleared")

    }

    fun dimBrightness(brightnessLevel: Float?) {
        val params : WindowManager.LayoutParams = window.attributes
        params.screenBrightness = brightnessLevel ?: 0.001f
        window.attributes = params
        Log.d("DimHide", "dimBrightness set to: ${params.screenBrightness}")

    }

}