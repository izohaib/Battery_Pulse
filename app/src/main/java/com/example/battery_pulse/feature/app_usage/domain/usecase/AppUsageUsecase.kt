package com.example.battery_pulse.feature.app_usage.domain.usecase

import com.example.battery_pulse.feature.app_usage.domain.models.AppUsageStat
import com.example.battery_pulse.feature.app_usage.domain.repository.AppUsageRepository
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class GetAppUsageUseCase @Inject constructor(
    private val repository: AppUsageRepository
) {
    suspend operator fun invoke(): List<AppUsageStat> =
        withContext(Dispatchers.IO) {
            repository.getTodayUsage()
        }
    fun hasPermission(): Boolean = repository.hasPermission()
}