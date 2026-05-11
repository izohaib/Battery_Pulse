package com.example.battery_pulse.feature.setting.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.battery_pulse.core.components.SectionHeader
import com.example.battery_pulse.feature.setting.domain.model.TemperatureUnit
import com.example.battery_pulse.feature.setting.domain.model.ThemeMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    viewModel: SettingViewModel = hiltViewModel(),
    navController: NavHostController
) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val s = settings ?: return

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ── Theme ─────────────────────────────────────────────────────
            SectionHeader(title = "Appearance", icon = Icons.Default.Palette)

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "App Theme",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(Modifier.height(6.dp))
                    SegmentedRow(
                        options = listOf("Light", "Dark", "System"),
                        selected = s.themeMode.ordinal,
                        onSelect = { viewModel.setTheme(ThemeMode.entries[it]) }
                    )
                }
            }

            // ── Charging Limit ────────────────────────────────────────────
            SectionHeader(title = "Charging", icon = Icons.Default.BatteryChargingFull)

            Card(modifier = Modifier.fillMaxWidth()) {
                Column {
                    SettingSwitchItem(
                        title = "Charging Limit Alert",
                        subtitle = "Notify when battery reaches the set limit",
                        icon = Icons.Default.NotificationsActive,
                        checked = s.chargeLimitEnabled,
                        onCheckedChange = viewModel::setChargeLimitEnabled
                    )
                    if (s.chargeLimitEnabled) {
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                        SliderItem(
                            title = "Limit",
                            value = s.chargeLimitPercent.toFloat(),
                            displayValue = "${s.chargeLimitPercent}%",
                            range = 50f..100f,
                            onValueChangeFinished = { viewModel.setChargeLimitPercent(it) }
                        )
                    }
                }
            }

            // ── Discharge Limit ───────────────────────────────────────────
            SectionHeader(title = "Battery Low", icon = Icons.Default.BatteryAlert)

            Card(modifier = Modifier.fillMaxWidth()) {
                Column {
                    SettingSwitchItem(
                        title = "Discharge Limit Alert",
                        subtitle = "Notify when battery drops to the set limit",
                        icon = Icons.Default.NotificationsActive,
                        checked = s.dischargeLimitEnabled,
                        onCheckedChange = viewModel::setDischargeLimitEnabled
                    )
                    if (s.dischargeLimitEnabled) {
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                        SliderItem(
                            title = "Limit",
                            value = s.dischargeLimitPercent.toFloat(),
                            displayValue = "${s.dischargeLimitPercent}%",
                            range = 5f..50f,
                            onValueChangeFinished = { viewModel.setDischargeLimitPercent(it) }
                        )
                    }
                }
            }

            // ── Temperature ───────────────────────────────────────────────
            SectionHeader(title = "Units", icon = Icons.Default.Thermostat)

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Temperature Unit",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        "Choose your preferred temperature unit",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(12.dp))
                    SegmentedRow(
                        options = listOf("Celsius", "Fahrenheit"),
                        selected = s.temperatureUnit.ordinal,
                        onSelect = { viewModel.setTemperatureUnit(TemperatureUnit.entries[it]) }
                    )
                }
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

// ── Reusable Components ───────────────────────────────────────────────────────


@Composable
private fun SettingSwitchItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(
                subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun SliderItem(
    title: String ,
    value: Float,
    displayValue: String,
    range: ClosedFloatingPointRange<Float>,
    onValueChangeFinished: (Int) -> Unit
) {
    var sliderValue by remember(value) { mutableFloatStateOf(value) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Surface(
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Text(
                    text = displayValue,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }
        Slider(
            value = sliderValue,
            onValueChange = { sliderValue = it },
            onValueChangeFinished = { onValueChangeFinished(sliderValue.toInt()) },
            valueRange = range,
            steps = (range.endInclusive - range.start).toInt() - 1,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun SegmentedRow(
    options: List<String>,
    selected: Int,
    onSelect: (Int) -> Unit
) {
    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
        options.forEachIndexed { index, label ->
            SegmentedButton(
                selected = selected == index,
                onClick = { onSelect(index) },
                shape = SegmentedButtonDefaults.itemShape(index, options.size),
                label = { Text(label) }
            )
        }
    }
}