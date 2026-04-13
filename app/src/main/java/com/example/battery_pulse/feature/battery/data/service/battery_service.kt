package com.example.battery_pulse.feature.battery.data.service


import android.app.AlarmManager
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
import android.os.SystemClock
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.battery_pulse.R
import com.example.battery_pulse.app.MainActivity


class BatteryService : Service() {

    private var alertShown = false


    // when battery will change or charge disconnected this will run and we launch it in
    // onStartCommand of foreground
    private val batteryReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {

            when (intent?.action) {

                Intent.ACTION_BATTERY_CHANGED -> {
                    val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0)
                    val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100)
                    val percent = (level * 100) / scale

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

                    // If charger was unplugged, stop everything
                    if (!isCharging && plugged == 0) {
                        stopSelf()
                        return
                    }

                    // Update live foreground notification
                    updateNotification("$percent% • $pluggedStatus • $healthStatus")

                    // Battery reached 100% — stop service
                    if (percent >= 100) {
                        showAlertNotification("Battery Full!", "Your battery is fully charged — unplug your charger")
                        stopSelf()
                        return
                    }

                    // Battery reached user set target
                    val targetPercent = getSharedPreferences("battery_prefs", MODE_PRIVATE)
                        .getInt("target_percent", 90)

                    if (percent >= targetPercent && !alertShown) {
                        alertShown = true
                        showAlertNotification(
                            "Battery Limit Reached!",
                            "Battery is at $percent% — unplug your charger"
                        )
                    }
                }

                Intent.ACTION_POWER_DISCONNECTED -> {
                    stopSelf()
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(1, createNotification("🔌 Charger connected"))

        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_BATTERY_CHANGED)
            addAction(Intent.ACTION_POWER_DISCONNECTED)
        }
        registerReceiver(batteryReceiver, filter)

        return START_NOT_STICKY
    }

    private fun updateNotification(text: String) {
        val manager = ContextCompat.getSystemService(this, NotificationManager::class.java)
        manager?.notify(1, createNotification(text))
    }

    private fun showAlertNotification(title: String, message: String) {
        val notification = NotificationCompat.Builder(this, "ID")
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        val manager = ContextCompat.getSystemService(this, NotificationManager::class.java)
        manager?.notify(2, notification)
    }

    fun createNotificationChannel() {
        val channel = NotificationChannel(
            "ID",
            "Zohaib Ahmad",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val manager = ContextCompat.getSystemService(this, NotificationManager::class.java)
        manager!!.createNotificationChannel(channel)
    }

    fun createNotification(text: String): Notification {
        return NotificationCompat.Builder(this, "ID")
            .setContentTitle("Battery Pulse")
            .setContentText(text)
            .setContentIntent(getPendingIntent())
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()
    }

    fun getPendingIntent(): PendingIntent {
        val intent = Intent(this, MainActivity::class.java)
        return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
    }

    override fun onDestroy() {
        super.onDestroy()
        try { unregisterReceiver(batteryReceiver) } catch (e: Exception) { }
    }

    override fun onBind(p0: Intent?): IBinder? = null
}

//    fun createNotification() {
//        createNotificationChannel()
//        startForeground(1, createNotification("🔌 Charger connected"))
//    }

//class BatteryService : Service() {
//
//    companion object {
//        const val CHANNEL_ID = "battery_channel"
//        const val FOREGROUND_NOTIF_ID = 1
//        const val ALERT_NOTIF_ID = 2
//    }
//
//    private var isCharging = false
//    private var alertShown = false
//
//    private val batteryReceiver = object : BroadcastReceiver() {
//        override fun onReceive(context: Context?, intent: Intent?) {
//            when (intent?.action) {
//
//                Intent.ACTION_POWER_CONNECTED -> {
//                    isCharging = true
//                    alertShown = false
//                    updateForegroundNotification("🔌 Device is charging")
//                }
//
//                Intent.ACTION_POWER_DISCONNECTED -> {
//                    isCharging = false
//                    alertShown = false
//                    updateForegroundNotification("Battery monitor active")
//                    getNotificationManager().cancel(ALERT_NOTIF_ID)
//                }
//
//                Intent.ACTION_BATTERY_CHANGED -> {
//                    val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0)
//                    val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100)
//                    val percent = (level * 100) / scale
//
//                    val target = getSharedPreferences("battery_prefs", MODE_PRIVATE)
//                        .getInt("target_percent", 89)
//
//                    if (isCharging && percent >= target && !alertShown) {
//                        alertShown = true
//                        showAlertNotification(percent)
//                    }
//                }
//            }
//        }
//    }
//
//    override fun onCreate() {
//        super.onCreate()
//        createNotificationChannel()
//    }
//
//    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        startForeground(FOREGROUND_NOTIF_ID, buildNotification("Battery monitor active"))
//
//        val filter = IntentFilter().apply {
//            addAction(Intent.ACTION_POWER_CONNECTED)
//            addAction(Intent.ACTION_POWER_DISCONNECTED)
//            addAction(Intent.ACTION_BATTERY_CHANGED)
//        }
//        registerReceiver(batteryReceiver, filter)
//
//        return START_STICKY // OS will restart service if killed
//    }
//
//    // Called when app is swiped — return START_STICKY so OS restarts
//    override fun onTaskRemoved(rootIntent: Intent?) {
//        super.onTaskRemoved(rootIntent)
//        val restartIntent = Intent(applicationContext, BatteryService::class.java)
//        val pendingIntent = PendingIntent.getService(
//            applicationContext, 1, restartIntent, PendingIntent.FLAG_IMMUTABLE
//        )
//        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
//        alarmManager.set(
//            AlarmManager.ELAPSED_REALTIME,
//            SystemClock.elapsedRealtime() + 500,
//            pendingIntent
//        )
//    }
//
//    private fun showAlertNotification(percent: Int) {
//        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
//            .setContentTitle("Battery Limit Reached!")
//            .setContentText("Battery is at $percent% — unplug your charger")
//            .setSmallIcon(R.drawable.ic_launcher_foreground)
//            .setPriority(NotificationCompat.PRIORITY_HIGH)
//            .setAutoCancel(true)
//            .build()
//        getNotificationManager().notify(ALERT_NOTIF_ID, notification)
//    }
//
//    private fun updateForegroundNotification(text: String) {
//        getNotificationManager().notify(FOREGROUND_NOTIF_ID, buildNotification(text))
//    }
//
//    private fun buildNotification(text: String): Notification {
//        val intent = PendingIntent.getActivity(
//            this, 0,
//            Intent(this, MainActivity::class.java),
//            PendingIntent.FLAG_IMMUTABLE
//        )
//        return NotificationCompat.Builder(this, CHANNEL_ID)
//            .setContentTitle("Battery Pulse")
//            .setContentText(text)
//            .setSmallIcon(R.drawable.ic_launcher_foreground)
//            .setContentIntent(intent)
//            .setOngoing(true)
//            .build()
//    }
//
//    private fun createNotificationChannel() {
//        val channel = NotificationChannel(
//            CHANNEL_ID, "Battery Monitor",
//            NotificationManager.IMPORTANCE_DEFAULT
//        )
//        getNotificationManager().createNotificationChannel(channel)
//    }
//
//    private fun getNotificationManager() =
//        getSystemService(NOTIFICATION_SERVICE) as NotificationManager
//
//    override fun onDestroy() {
//        super.onDestroy()
//        try { unregisterReceiver(batteryReceiver) } catch (e: Exception) { }
//    }
//
//    override fun onBind(intent: Intent?): IBinder? = null
//}
//
//class BootReceiver : BroadcastReceiver() {
//    override fun onReceive(context: Context, intent: Intent?) {
//        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
//            context.startForegroundService(Intent(context, BatteryService::class.java))
//        }
//    }
//}