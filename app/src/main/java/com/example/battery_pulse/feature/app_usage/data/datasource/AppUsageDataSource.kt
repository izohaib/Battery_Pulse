package com.example.battery_pulse.feature.app_usage.data.datasource

import android.app.AppOpsManager
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.battery_pulse.feature.app_usage.domain.models.AppUsageStat
import com.example.battery_pulse.feature.app_usage.domain.repository.AppUsageRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import jakarta.inject.Singleton
import java.util.Calendar

@Singleton
class AppUsageLocalDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) {

     fun getTodayUsage(): Map<String, UsageStats>{
        val usageStatsManager =
            ContextCompat.getSystemService(context, UsageStatsManager::class.java)

        val startOfDay = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        val now = System.currentTimeMillis()

        // Merge all intervals — keep highest foreground time per package
        val merged = mutableMapOf<String, UsageStats>()
        val intervals = listOf(
            UsageStatsManager.INTERVAL_DAILY,
//            UsageStatsManager.INTERVAL_BEST,
//            UsageStatsManager.INTERVAL_WEEKLY
        )

        intervals.forEach { interval ->
            // android gave us usage stats list of all apps
            val usageStatsList: List<UsageStats> = usageStatsManager?.queryUsageStats(
                interval,
                startOfDay,
                now
            ) ?: emptyList()

            usageStatsList.forEach { stat ->
                Log.d(
                    "USAGE_STATS",
                    "App: ${stat.packageName} | Time: ${stat.totalTimeInForeground}"
                )
                val existing = merged[stat.packageName]
                if (existing == null || stat.totalTimeInForeground > existing.totalTimeInForeground) {
                    merged[stat.packageName] = stat
                }
            }
        }



        return merged

    }
}