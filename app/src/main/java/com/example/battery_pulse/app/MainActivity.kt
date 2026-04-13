package com.example.battery_pulse.app

import android.annotation.SuppressLint
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
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.battery_pulse.core.ui.theme.BatterypulseTheme
import com.example.battery_pulse.app.ui.main.MainScreen
import com.example.battery_pulse.feature.battery.data.datasource.BatteryDataSource
import com.example.battery_pulse.feature.battery.data.repositoryImpl.BatteryRepositoryImpl
import com.example.battery_pulse.feature.battery.data.service.BatteryService
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

//        // Start service
//        startForegroundService(Intent(this, BatteryService::class.java))
//
//        // Ask battery optimization exemption
//        requestBatteryOptimizationExemption()

//        val filter = IntentFilter().apply {
//            addAction(Intent.ACTION_BATTERY_CHANGED)
//            addAction(Intent.ACTION_POWER_CONNECTED)
//            addAction(Intent.ACTION_POWER_DISCONNECTED)
//        }
//        registerReceiver(PowerReceiver(), filter)

//        val intent = Intent(this, BatteryService::class.java)
//        startForegroundService(intent)
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
