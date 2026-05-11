package com.example.battery_pulse.app

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.app.ActivityCompat
import androidx.core.view.WindowCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.battery_pulse.core.ui.theme.BatterypulseTheme
import com.example.battery_pulse.app.ui.main.MainScreen
import com.example.battery_pulse.feature.battery.data.datasource.BatteryDataSource
import com.example.battery_pulse.core.Service.BatteryService
import com.example.battery_pulse.feature.battery.data.repositoryImpl.BatteryRepositoryImpl
import com.example.battery_pulse.feature.battery.domain.usecase.GetBatteryInfoUseCase
import com.example.battery_pulse.feature.battery.presentaion.BatteryViewModel
import com.example.battery_pulse.feature.setting.domain.model.ThemeMode
import com.example.battery_pulse.feature.setting.presentation.SettingViewModel
import dagger.hilt.android.AndroidEntryPoint

//import com.example.battery_pulse.feature.battery.data.service.PowerReceiver

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    fun dimBrightness(brightnessLevel: Float?) {
        val params: WindowManager.LayoutParams = window.attributes
        params.screenBrightness = brightnessLevel ?: 0.2f
        window.attributes = params
        Log.d("DimHide", "dimBrightness set to: ${params.screenBrightness}")

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        val splashScreen = installSplashScreen()

        enableEdgeToEdge()


//        val controller = WindowCompat.getInsetsController(window, window.decorView)
//
//// hide status bar
//        controller.hide(WindowInsetsCompat.Type.statusBars())


        // 1. CREATE dependencies first (DI setup)
//        val dataSource = BatteryDataSource(applicationContext)
//        val repository = BatteryRepositoryImpl(dataSource)
//        val useCase = GetBatteryInfoUseCase(repository)
//
//        val viewModel = BatteryViewModel(useCase, repository)


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

            val settingsViewModel: SettingViewModel = hiltViewModel()
            val settings by settingsViewModel.settings.collectAsState()

            val darkTheme = when (settings?.themeMode) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
                else -> isSystemInDarkTheme()
            }


            BatterypulseTheme(
                darkTheme = darkTheme
            ) {
                val viewModel: BatteryViewModel = hiltViewModel()
                MainScreen(viewModel)
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
