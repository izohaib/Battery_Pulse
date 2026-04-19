package com.example.battery_pulse.feature.battery.presentaion

import android.os.Build
import android.os.Build.MANUFACTURER
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.battery_pulse.feature.battery.domain.models.BatteryInfo
import androidx.compose.runtime.getValue
import com.example.battery_pulse.core.Utils.formatMinutes
import com.example.battery_pulse.feature.battery.presentaion.components.HeroSection
import kotlin.math.abs

@Composable
fun BatteryScreen(navController: NavController, viewModel: BatteryViewModel) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 24.dp)
    ) {
        when (state) {
            is BatteryUiState.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
            is BatteryUiState.Error -> {
                val msg = (state as BatteryUiState.Error).message
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(msg, color = MaterialTheme.colorScheme.error)
                }
            }
            is BatteryUiState.Success -> {
                val data = (state as BatteryUiState.Success).data
                BatteryContent(data)
            }
        }
    }
}

@Composable
fun BatteryContent(data: BatteryInfo) {
    val estimatedTimeFormat = formatMinutes(data.timeToFullMinutes ?: 0)
    val currentMa = data.currentMicroAmps.toDouble()
    val absWatts = abs(data.chargingWatts)
    val powerUnit = if (absWatts < 1f) "mW" else "W"
    val powerValue = if (absWatts < 1f)
        "%.0f".format(data.chargingWatts * 1000)
    else
        "%.2f".format(data.chargingWatts)

    HeroSection(
        percent = data.percent,
        chargingWatts = data.chargingWatts,
        isCharging = data.isCharging
    )

    Spacer(Modifier.height(24.dp))

    SectionLabel("Live Stats")
    Spacer(Modifier.height(8.dp))

    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        MetricCard("Temperature", "%.1f".format(data.temperatureCelsius), "°C", Modifier.weight(1f))
        MetricCard("Voltage", "${data.voltageMilliVolts}", "mV", Modifier.weight(1f))
    }

    Spacer(Modifier.height(10.dp))

    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        MetricCard("Current", "%.0f".format(currentMa), "mA", Modifier.weight(1f))
        MetricCard("Power", powerValue, powerUnit, Modifier.weight(1f))
    }

    Spacer(Modifier.height(24.dp))

    SectionLabel("Details")
    Spacer(Modifier.height(8.dp))

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
            val manufacturer = Build.MANUFACTURER
            DetailRow("Health", data.health, isFirst = true)
            DetailRow("Plug type", data.plugType)
            DetailRow("Status", if (data.isCharging) "Charging" else "Unplugged")
            DetailRow("Manufacturer", "$manufacturer")
            DetailRow(
                "Time to full",
                if (data.timeToFullMinutes != null) "~$estimatedTimeFormat" else "—"
            )
        }
    }
}

@Composable
private fun SectionLabel(title: String) {
    Text(
        text = title,
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(horizontal = 20.dp)
    )
}

@Composable
fun MetricCard(label: String, value: String, unit: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    value,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    " $unit",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String, isFirst: Boolean = false) {
    if (!isFirst) {
        HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant,
            thickness = 0.5.dp
        )
    }
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(
            value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = if (label == "Health" && value == "Good")
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.onSurface
        )
    }
}