package com.example.battery_pulse.feature.app_usage.domain.models

import android.graphics.drawable.Drawable

data class AppUsageStat(
    val packageName: String,
    val appName: String,
    val icon: Drawable?,
    val totalTimeInForegroundMs: Long,   // raw ms used today
    val estimatedBatteryPercent: Float   // calculated share of drain
)