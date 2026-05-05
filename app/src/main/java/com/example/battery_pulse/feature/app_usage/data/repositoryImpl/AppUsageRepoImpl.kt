package com.example.battery_pulse.feature.app_usage.data.repositoryImpl

import android.app.AppOpsManager
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.battery_pulse.feature.app_usage.data.datasource.AppUsageLocalDataSource
import com.example.battery_pulse.feature.app_usage.domain.models.AppUsageStat
import com.example.battery_pulse.feature.app_usage.domain.repository.AppUsageRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import jakarta.inject.Singleton
import java.util.Calendar

@Singleton
class AppUsageRepositoryImpl @Inject constructor(
    private val dataSource: AppUsageLocalDataSource,
    @param:ApplicationContext private val context: Context
) : AppUsageRepository {

    override fun hasPermission(): Boolean {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            android.os.Process.myUid(),
            context.packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }

//    override suspend fun getTodayUsage(): List<AppUsageStat> {
//        val usageStatsManager =
//            ContextCompat.getSystemService(context, UsageStatsManager::class.java)
//        val pm = context.packageManager
//
//        val startOfDay = Calendar.getInstance().apply {
//            set(Calendar.HOUR_OF_DAY, 0)
//            set(Calendar.MINUTE, 0)
//            set(Calendar.SECOND, 0)
//            set(Calendar.MILLISECOND, 0)
//        }.timeInMillis
//        val now = System.currentTimeMillis()
//
//        // This API respects your exact time range unlike queryUsageStats
//        val aggregated: Map<String, UsageStats> = usageStatsManager
//            ?.queryAndAggregateUsageStats(startOfDay, now)
//            ?: emptyMap()
//
//        val result: List<AppUsageStat> = aggregated.values
//            .filter { it.totalTimeInForeground > 0 }
//            .map { stat ->
//                val appName = try {
//                    val info = pm.getApplicationInfo(stat.packageName, 0)
//                    pm.getApplicationLabel(info).toString()
//                } catch (e: Exception) {
//                    val segments = stat.packageName.split(".")
//                    val last = segments.lastOrNull() ?: stat.packageName
//                    val secondLast = segments.getOrNull(segments.size - 2)
//                    val raw = if (last.lowercase() in listOf("android", "app", "main", "core")) {
//                        secondLast ?: last
//                    } else last
//                    raw.replaceFirstChar { it.uppercase() }
//                }
//
//                val icon = try {
//                    pm.getApplicationIcon(stat.packageName)
//                } catch (e: Exception) {
//                    null
//                }
//
//                AppUsageStat(
//                    packageName = stat.packageName,
//                    appName = appName,
//                    icon = icon,
//                    totalTimeInForegroundMs = stat.totalTimeInForeground,
//                    estimatedBatteryPercent = 0f
//                )
//            }
//            .sortedByDescending { it.totalTimeInForegroundMs }
//
//        result.forEach { app ->
//            Log.d("APP_RESULT", "Name: ${app.appName}, Package: ${app.packageName}, Time: ${app.totalTimeInForegroundMs}")
//        }
//
//        return result
//    }

    override suspend fun getTodayUsage(): List<AppUsageStat> {

        val pm = context.packageManager
        val merged = dataSource.getTodayUsage()

        val result: List<AppUsageStat> = merged.values // .values make map to list with only value
            .filter { it.totalTimeInForeground > 0 }
            .map { stat ->

                val appName = try {
                    val info = pm.getApplicationInfo(stat.packageName, 0)
                    pm.getApplicationLabel(info).toString()
//                    Log.d("APP_Name", "Package: ${stat.packageName} | AppName: $name")

                } catch (e: Exception) {
                    // Better fallback: use second-to-last segment if last is generic
                    val segments = stat.packageName.split(".")
                    val last = segments.lastOrNull() ?: stat.packageName
                    val secondLast = segments.getOrNull(segments.size - 2)

                    // If last segment is generic like "android", use second-to-last
                    val raw = if (last.lowercase() in listOf("android", "app", "main", "core")) {
                        secondLast ?: last
                    } else last

                    raw.replaceFirstChar { it.uppercase() }
                }

                val icon = try {
                    pm.getApplicationIcon(stat.packageName)
                } catch (e: Exception) {
                    null  // Return null, not default icon — UI already handles null with ?.let
                }
                Log.d(
                    "NAME_DEBUG", "pkg=${stat.packageName} | label=${
                        try {
                            val info = pm.getApplicationInfo(
                                stat.packageName,
                                PackageManager.GET_META_DATA
                            )
                            pm.getApplicationLabel(info).toString()
                        } catch (e: Exception) {
                            "EXCEPTION: ${e.message}"
                        }
                    }"
                )

                AppUsageStat(
                    packageName = stat.packageName,
                    appName = appName,
                    icon = icon,
                    totalTimeInForegroundMs = stat.totalTimeInForeground,
                    estimatedBatteryPercent = 0f
                )
            }
            .sortedByDescending { it.totalTimeInForegroundMs }

        result.forEach { app ->
            Log.d(
                "APP_RESULT",
                "Name: ${app.appName}, Package: ${app.packageName}, Time: ${app.totalTimeInForegroundMs}"
            )
        }

        return result

    }
}