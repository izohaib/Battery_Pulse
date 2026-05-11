package com.example.battery_pulse.core.Service


import android.annotation.SuppressLint
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.IBinder
import android.util.Log
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.os.Handler
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.battery_pulse.R
import com.example.battery_pulse.app.MainActivity
import com.example.battery_pulse.feature.on_display.presentation.fullScreenIntentActivity.ChargingDisplayActivity
import com.example.battery_pulse.feature.history.domain.repository.ChargingHistoryRepository
import com.example.battery_pulse.feature.setting.domain.model.SettingsEntity
import com.example.battery_pulse.feature.setting.domain.repository.SettingsRepository
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

// for foreground service and full screen intent launching


@EntryPoint
@InstallIn(SingletonComponent::class)
interface BatteryServiceEntryPoint {
    fun chargingHistoryRepository(): ChargingHistoryRepository
}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface SettingsServiceEntryPoint {
    fun settingsRepository(): SettingsRepository
}

class BatteryService : Service() {
    private var dischargeAlertShown = false
    private var alertShown = false
    private var fullAlertShown = false


    private var currentSettings = SettingsEntity()

    companion object {
        const val FOREGROUND_CHANNEL_ID = "battery_foreground"
        const val ALERT_CHANNEL_ID = "battery_alert"
        const val CHARGING_DISPLAY_CHANNEL_ID = "charging_display"
        const val CHARGING_DISPLAY_NOTIF_ID = 99

    }

    private var lastPercent = 0  // track latest percent

    // when battery will change or charge disconnected this will run, we launch it in
    // onStartCommand of foreground


    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private fun getHistoryRepository(): ChargingHistoryRepository {
        return EntryPointAccessors.fromApplication(
            applicationContext,
            BatteryServiceEntryPoint::class.java
        ).chargingHistoryRepository()
    }

    private fun getSettingsRepository(): SettingsRepository {
        return EntryPointAccessors.fromApplication(
            applicationContext,
            SettingsServiceEntryPoint::class.java
        ).settingsRepository()
    }

    private fun observeSettings() {
        serviceScope.launch {                      // ← ONE coroutine, started once
            getSettingsRepository().getSettings()
                .collect { settings ->             // ← ONE collector, runs forever
                    currentSettings = settings     // ← just updates a variable
                }
        }
    }

    private val batteryReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                Intent.ACTION_BATTERY_CHANGED -> {
                    val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0)
                    val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100)
                    val percent = (level * 100) / scale
                    lastPercent = percent
                    Log.d(
                        "BatteryService",
                        "BATTERY_CHANGED: percent=$percent, lastPercent=$lastPercent"
                    )

                    val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
                    val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING
                            || status == BatteryManager.BATTERY_STATUS_FULL

                    val plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)
                    val pluggedStatus = when (plugged) {
                        BatteryManager.BATTERY_PLUGGED_AC -> "AC"
                        BatteryManager.BATTERY_PLUGGED_USB -> "USB"
                        BatteryManager.BATTERY_PLUGGED_WIRELESS -> "Wireless"
                        else -> "Unplugged"
                    }

                    val health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1)
                    val healthStatus = when (health) {
                        BatteryManager.BATTERY_HEALTH_GOOD -> "Good"
                        BatteryManager.BATTERY_HEALTH_OVERHEAT -> "Overheat"
                        BatteryManager.BATTERY_HEALTH_DEAD -> "Dead"
                        BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "Over Voltage"
                        else -> "Unknown"
                    }

                    // If charger was unplugged, stop entire Service and cancel notification
//                    if (!isCharging && plugged == 0) {
//                        val manager = ContextCompat.getSystemService(this@BatteryService, NotificationManager::class.java)
//                        manager?.cancel(2)
//                        stopSelf() // stop entire service
//                        return
//                    }

                    // Update live foreground notification
                    updateForegroundNotification("$percent% • $pluggedStatus • $healthStatus")

                    // Battery reached 100% — stop service
                    if (percent >= 100 && !fullAlertShown) {
                        fullAlertShown = true
                        showAlertNotification(
                            "Battery Full!",
                            "Your battery is fully charged — unplug your charger"
                        )

                        return
                    }
                    if (percent < 100) fullAlertShown = false

                    // notification battery reached
                    // Charge limit notification
                    if (isCharging && currentSettings.chargeLimitEnabled &&
                        percent >= currentSettings.chargeLimitPercent && !alertShown
                    ) {
                        alertShown = true
                        showAlertNotification(
                            "Battery Limit Reached!",
                            "Battery is at $percent% — unplug your charger"
                        )
                    }
                    if (percent < currentSettings.chargeLimitPercent) alertShown = false

// Discharge limit notification
                    if (!isCharging && currentSettings.dischargeLimitEnabled &&
                        percent <= currentSettings.dischargeLimitPercent && !dischargeAlertShown
                    ) {
                        dischargeAlertShown = true
                        showAlertNotification(
                            "Battery Low!",
                            "Battery dropped to $percent% — plug in your charger"
                        )
                    }
                    if (percent > currentSettings.dischargeLimitPercent) dischargeAlertShown = false
                    // Battery reached user set target
//                    val targetPercent = getSharedPreferences("battery_prefs", MODE_PRIVATE)
//                        .getInt("target_percent", 90)
//
//                    if (percent >= targetPercent && !alertShown) {
//                        alertShown = true
//                        showAlertNotification(
//                            "Battery Limit Reached!",
//                            "Battery is at $percent% — unplug your charger"
//                        )
//                    }


                    // sends a message to anyone in the app who is listening
                    // ADD: send live update to ChargingDisplayActivity if it's open
                    val updateIntent = Intent("ACTION_CHARGING_UPDATE").apply {
                        putExtra("percent", percent)
                    }
                    // shouting listen it whole app
                    sendBroadcast(updateIntent)

                }

                Intent.ACTION_POWER_CONNECTED -> {

                    serviceScope.launch {
                        val repo = getHistoryRepository()
                        if (!repo.hasOpenSession()) {
                            repo.startSession(
                                startTime = System.currentTimeMillis(),
                                startBattery = lastPercent
                            )
                        }
                    }

                    // user plugged charger during grace period → cancel the stop timer
                    stopHandler.removeCallbacks(stopRunnable)
                    alertShown = false
                    fullAlertShown = false
                    updateForegroundNotification("🔌 Charger connected")

                    Log.d("BatteryService", "POWER_CONNECTED fired — lastPercent=$lastPercent")
                    Log.d("BatteryService", "Calling launchChargingDisplay...")
                    // ADD: launch charging display notification
                    launchChargingDisplay(lastPercent)
                }


                Intent.ACTION_POWER_DISCONNECTED -> {

                    serviceScope.launch {
                        getHistoryRepository().endSession(
                            endTime = System.currentTimeMillis(),
                            endBattery = lastPercent
                        )
                    }

                    // If charger was unplugged, stop entire Service and cancel notification
                    val manager = ContextCompat.getSystemService(
                        this@BatteryService,
                        NotificationManager::class.java
                    )
                    manager?.cancel(2)
//                        stopSelf() // stop entire service
//                        return
//

                    // ADD: cancel charging display notification
                    manager?.cancel(CHARGING_DISPLAY_NOTIF_ID)

                    // ADD: tell activity to close itself
                    sendBroadcast(Intent("ACTION_DISMISS_CHARGING_DISPLAY"))


                    // start 1-hour timer
                    stopHandler.postDelayed(stopRunnable, 60 * 60 * 1000L)
                }


            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        Log.d("Created Notification Channel", "Creating Foreground Service")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("Starting Foreground Service", "Starting Foreground Service")
        alertShown = false

        try {
            unregisterReceiver(batteryReceiver)
        } catch (e: Exception) {
        }

        stopHandler.removeCallbacks(stopRunnable)
        observeSettings()

        //internally called manager?.notify(1, notification)
        startForeground(1, createForegroundNotification("🔌 Charger connected"))

        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_BATTERY_CHANGED)
            addAction(Intent.ACTION_POWER_CONNECTED)
            addAction(Intent.ACTION_POWER_DISCONNECTED)
        }
        registerReceiver(batteryReceiver, filter)

        return START_STICKY
    }

    private fun updateForegroundNotification(text: String) {
        val manager = ContextCompat.getSystemService(this, NotificationManager::class.java)
        manager?.notify(1, createForegroundNotification(text))
    }

    fun createNotificationChannel() {
        val manager = ContextCompat.getSystemService(this, NotificationManager::class.java)


        val foregroundChannel = NotificationChannel(
            FOREGROUND_CHANNEL_ID,
            "Zohaib Ahmad",
            NotificationManager.IMPORTANCE_LOW
        )

        val alertChannel = NotificationChannel(
            ALERT_CHANNEL_ID,
            "Battery Alerts",
            NotificationManager.IMPORTANCE_HIGH
        )

        val chargingDisplayChannel = NotificationChannel(
            CHARGING_DISPLAY_CHANNEL_ID,
            "Charging Display",
            NotificationManager.IMPORTANCE_HIGH  // must be HIGH for fullScreenIntent
        ).apply {
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC // show on lockscreen
        }

        manager?.createNotificationChannel(chargingDisplayChannel)
        manager?.createNotificationChannel(foregroundChannel)
        manager?.createNotificationChannel(alertChannel)

    }


    @SuppressLint("FullScreenIntentPolicy")
    private fun launchChargingDisplay(percent: Int) {
        Log.d("BatteryService", "launchChargingDisplay() called with percent=$percent")

        val activityIntent = Intent(this, ChargingDisplayActivity::class.java).apply {
            addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_SINGLE_TOP or
                        Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
            )
            putExtra("percent", percent)
        }

        // Approach 1 — try direct startActivity first
        // Works when: app is in foreground OR device allows background launches
        try {
            startActivity(activityIntent)
            Log.d("BatteryService", "startActivity() called directly")
        } catch (e: Exception) {
            Log.e("BatteryService", "startActivity failed: ${e.message}")
        }

        // Approach 2 — fullScreenIntent notification (works on screen off / lockscreen)
        // Both run together — activity launch + notification as backup
        val fullScreenPendingIntent = PendingIntent.getActivity(
            this,
            CHARGING_DISPLAY_NOTIF_ID,
            activityIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHARGING_DISPLAY_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Battery Charging")
            .setContentText("$percent% • Tap to view")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .setOngoing(true)
            .setAutoCancel(false)
            .build()

        val manager = ContextCompat.getSystemService(this, NotificationManager::class.java)
        manager?.notify(CHARGING_DISPLAY_NOTIF_ID, notification)
        Log.d("BatteryService", "Notification fired with ID=$CHARGING_DISPLAY_NOTIF_ID")
    }

    fun createForegroundNotification(text: String): Notification {
        return NotificationCompat.Builder(this, FOREGROUND_CHANNEL_ID)
            .setContentTitle("Battery Pulse")
            .setContentText(text)
            .setContentIntent(getPendingIntent())
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()
    }

    private fun showAlertNotification(title: String, message: String) {
        val notification = NotificationCompat.Builder(this, ALERT_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        val manager = ContextCompat.getSystemService(this, NotificationManager::class.java)
        manager?.notify(2, notification)
    }

    fun getPendingIntent(): PendingIntent {
        val intent = Intent(this, MainActivity::class.java)
        return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
    }

    private val stopHandler = Handler(Looper.getMainLooper())
    private val stopRunnable = Runnable {
        val bm = getSystemService(BATTERY_SERVICE) as BatteryManager
        if (!bm.isCharging) {
            val manager = ContextCompat.getSystemService(this, NotificationManager::class.java)
//            manager?.cancel(2)
            stopSelf()
        }
    }


    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        val bm = getSystemService(BATTERY_SERVICE) as BatteryManager
        if (!bm.isCharging) {
            // wait 1 hour, then stop if still not charging
            stopHandler.postDelayed(stopRunnable, 60 * 60 * 1000L)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopHandler.removeCallbacks(stopRunnable)
        try {
            unregisterReceiver(batteryReceiver)
        } catch (e: Exception) {
        }
    }

    override fun onBind(p0: Intent?): IBinder? = null
}
