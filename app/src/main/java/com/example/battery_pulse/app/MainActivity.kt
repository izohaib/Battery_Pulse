package com.example.battery_pulse.app

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.app.ActivityCompat
import com.example.battery_pulse.core.ui.theme.BatterypulseTheme
import com.example.battery_pulse.app.ui.main.MainScreen
import com.example.battery_pulse.feature.battery.data.datasource.BatteryDataSource
import com.example.battery_pulse.feature.battery.data.notification_service.BatteryService
import com.example.battery_pulse.feature.battery.data.repositoryImpl.BatteryRepositoryImpl
import com.example.battery_pulse.feature.battery.domain.usecase.GetBatteryInfoUseCase
import com.example.battery_pulse.feature.battery.presentaion.BatteryViewModel

//import com.example.battery_pulse.feature.battery.data.service.PowerReceiver

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        val splashScreen = installSplashScreen()

        enableEdgeToEdge()


        // 1. CREATE dependencies first (DI setup)
        val dataSource = BatteryDataSource(applicationContext)
        val repository = BatteryRepositoryImpl(dataSource)
        val useCase = GetBatteryInfoUseCase(repository)

        val viewmodel = BatteryViewModel(useCase, repository)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                200
            )

        }



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                startActivity(
                    Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:$packageName")
                    )
                )
            }
        }

        // ADD after POST_NOTIFICATIONS request
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) { // API 34
            val notifManager = getSystemService(NotificationManager::class.java)
            if (!notifManager.canUseFullScreenIntent()) {
                val intent = Intent(Settings.ACTION_MANAGE_APP_USE_FULL_SCREEN_INTENT).apply {
                    data = Uri.parse("package:$packageName")
                }
                startActivity(intent)
            }
        }

//        // Start service
//        startForegroundService(Intent(this, BatteryService::class.java))
//
//        // Ask battery optimization exemption
//        requestBatteryOptimizationExemption()



        val intent = Intent(this, BatteryService::class.java)
        startForegroundService(intent)
        // 2. Keep splash visible until ViewModel says data is ready
//        splashScreen.setKeepOnScreenCondition {
//            !viewModel.isReady  // splash stays while isReady == false
//        }
        setContent {
            BatterypulseTheme {
                MainScreen(viewmodel)
            }
        }
    }

    @SuppressLint("ServiceCast")
    private fun requestBatteryOptimizationExemption() {
        val pm = getSystemService(POWER_SERVICE) as PowerManager
        val packageName = packageName

        if (!pm.isIgnoringBatteryOptimizations(packageName)) {
            val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                data = Uri.parse("package:$packageName")
            }
            startActivity(intent)
        }
    }
}
