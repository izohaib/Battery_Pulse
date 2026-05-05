package com.example.battery_pulse.feature.app_usage.domain.repository

import com.example.battery_pulse.feature.app_usage.domain.models.AppUsageStat

interface AppUsageRepository {
    suspend fun getTodayUsage(): List<AppUsageStat>
    fun hasPermission(): Boolean
}