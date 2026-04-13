package com.example.battery_pulse.feature.battery.presentaion

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.navigation.NavController
import com.example.battery_pulse.feature.battery.domain.models.BatteryInfo
import androidx.compose.runtime.getValue

@Composable
fun BatteryScreen(navController: NavController, viewModel: BatteryViewModel) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 24.dp)
    ) {
        when (state) {
            is BatteryUiState.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
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

    // header
    Row(
        modifier = Modifier.fillMaxWidth().padding(20.dp, 24.dp, 20.dp, 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text("Battery Pulse", fontSize = 22.sp, fontWeight = FontWeight.Medium)
            Text("Live battery monitor", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        if (data.isCharging && data.timeToFullMinutes != null) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Text(
                    "~${data.timeToFullMinutes} min left",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }

    // hero card
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // percent
            Row(verticalAlignment = Alignment.Bottom) {
                Text("${data.percent}", fontSize = 42.sp, fontWeight = FontWeight.Medium)
                Text("%", fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 6.dp))
            }

            // progress bar
            LinearProgressIndicator(
                progress = { data.percent / 100f },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(99.dp)),
                color = if (data.percent > 20) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            )

            // charging badge
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = if (data.isCharging)
                    MaterialTheme.colorScheme.primaryContainer
                else
                    MaterialTheme.colorScheme.surfaceVariant
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .background(
                                if (data.isCharging) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurfaceVariant,
                                CircleShape
                            )
                    )
                    Text(
                        if (data.isCharging) "Charging via ${data.plugType}" else "Not charging",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (data.isCharging)
                            MaterialTheme.colorScheme.onPrimaryContainer
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }

    Spacer(Modifier.height(20.dp))

    // live stats grid
    Text("Live stats", fontSize = 12.sp, fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(horizontal = 20.dp),
        letterSpacing = 0.06.sp)

    Spacer(Modifier.height(8.dp))

    // 2x2 grid
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        MetricCard("Temperature", "%.1f".format(data.temperatureCelsius), "°C", Modifier.weight(1f))
        MetricCard("Voltage", "${data.voltageMilliVolts}", "mV", Modifier.weight(1f))
    }
    Spacer(Modifier.height(10.dp))
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        MetricCard("Current", "${Math.abs(data.currentMicroAmps / 1000)}", "mA", Modifier.weight(1f))
        MetricCard("Power", "%.2f".format(data.chargingWatts), "W", Modifier.weight(1f))
    }

    Spacer(Modifier.height(20.dp))

    // details card
    Text("Details", fontSize = 12.sp, fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(horizontal = 20.dp))

    Spacer(Modifier.height(8.dp))

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
            DetailRow("Health", data.health, isFirst = true)
            DetailRow("Plug type", data.plugType)
            DetailRow("Status", if (data.isCharging) "Charging" else "Unplugged")
            DetailRow("Time to full", if (data.timeToFullMinutes != null) "~${data.timeToFullMinutes} min" else "—")
        }
    }
}

@Composable
fun MetricCard(label: String, value: String, unit: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(value, fontSize = 20.sp, fontWeight = FontWeight.Medium)
                Text(" $unit", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 2.dp))
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String, isFirst: Boolean = false) {
    if (!isFirst) {
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 0.5.dp)
    }
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.Medium,
            color = if (label == "Health" && value == "Good")
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.onSurface
        )
    }
}