package com.example.battery_pulse.feature.on_display.presentation

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.battery_pulse.core.utils.previewDate
import com.example.battery_pulse.feature.on_display.presentation.fullScreenIntentActivity.ChargingDisplayActivity
import com.example.battery_pulse.feature.on_display.presentation.components.PhoneMockupCard
import com.example.battery_pulse.core.components.SectionHeader


// ─────────────────────────────────────────────────────────────────────────────
// Screen
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DisplayScreen(
    navController: NavController,
    viewModel: OnDisplayViewModel = hiltViewModel()
) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val battery by viewModel.batteryInfo.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val s = settings ?: return

    var showDisplaySettingsSheet by remember { mutableStateOf(false) }
    var showTimingSheet by remember { mutableStateOf(false) }
    var showDateFormatSheet by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {

        Row(
            verticalAlignment = Alignment.Top,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                PhoneMockupCard(
                    settings = s,
                    batteryInfo = battery,
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .height(220.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.Start
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Live Preview",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (s.alwaysOnDisplay) "AoD · On" else "AoD · Off",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (s.alwaysOnDisplay)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    FilledTonalButton(
                        onClick = {
                            context.startActivity(
                                Intent(context, ChargingDisplayActivity::class.java)
                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            )
                        },
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            Icons.Default.OpenInFull,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text("Full Screen", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        }
        HorizontalDivider()

        // ── Scrollable Settings ───────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ── Always-on Display ─────────────────────────────────────────
            SectionHeader("Always-on Display")
            Card(modifier = Modifier.fillMaxWidth()) {
                SettingSwitchItem(
                    title = "Always-on Display",
                    subtitle = "Keep display active while charging",
                    icon = Icons.Default.ScreenLockPortrait,
                    checked = s.alwaysOnDisplay,
                    onCheckedChange = { viewModel.setAlwaysOnDisplay(it) }
                )
            }

            // ── Display Content ───────────────────────────────────────────
            SectionHeader("Display Content")
            Card(modifier = Modifier.fillMaxWidth()) {
                Column {
                    SettingsNavItem(
                        title = "Display Info Settings",
                        subtitle = "Choose what to show on screen",
                        icon = Icons.Default.Tune,
                        onClick = { showDisplaySettingsSheet = true }
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    SettingSwitchItem(
                        title = "Hide Clock",
                        subtitle = "Remove clock from the display",
                        icon = Icons.Default.AlarmOff,
                        checked = s.hideClock,
                        onCheckedChange = { viewModel.setHideClock(it) }
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    SettingSwitchItem(
                        title = "Use 24-Hour Format",
                        subtitle = "Switch to 24-hour time notation",
                        icon = Icons.Default.AccessTime,
                        checked = s.is24HourFormat,
                        onCheckedChange = { viewModel.set24HourFormat(it) }
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    SettingsNavItem(
                        title = "Date Formatting",
                        subtitle = previewDate(s.dateFormat),
                        icon = Icons.Default.CalendarToday,
                        onClick = { showDateFormatSheet = true }
                    )
                }
            }

            // ── Screen Timing ─────────────────────────────────────────────
            SectionHeader("Screen Timing")
            Card(modifier = Modifier.fillMaxWidth()) {
                SettingsNavItem(
                    title = "Dim & Hide Timings",
                    subtitle = "Control when the screen dims or hides",
                    icon = Icons.Default.Schedule,
                    onClick = { showTimingSheet = true }
                )
            }

            // ── Quick Actions ─────────────────────────────────────────────
            SectionHeader("Quick Actions")
            Card(modifier = Modifier.fillMaxWidth()) {
                Column {
                    SettingSwitchItem(
                        title = "Torch (Tap & Hold)",
                        subtitle = "Toggle flashlight with a long press",
                        icon = Icons.Default.FlashOn,
                        checked = s.showTorch,
                        onCheckedChange = { viewModel.showTorch(it) }
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    SettingSwitchItem(
                        title = "Camera (Tap & Hold)",
                        subtitle = "Open camera with a long press",
                        icon = Icons.Default.CameraAlt,
                        checked = s.showCamera,
                        onCheckedChange = { viewModel.showCamera(it) }
                    )
                }
            }

            Spacer(Modifier.height(8.dp))
        }
    }

    // ── Bottom Sheets
    if (showDisplaySettingsSheet) {
        ModalBottomSheet(
            onDismissRequest = { showDisplaySettingsSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 32.dp)
            ) {
                Text(
                    text = "Display Info Settings",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = "Pick the battery stats shown on your display",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        TickSettingItem(
                            title = "Temperature",
                            subtitle = "Show battery temperature",
                            icon = Icons.Default.DeviceThermostat,
                            selected = s.showTemperature,
                            onClick = { viewModel.setShowTemperature(!s.showTemperature) }
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                        TickSettingItem(
                            title = "Voltage",
                            subtitle = "Show battery voltage",
                            icon = Icons.Default.Bolt,
                            selected = s.showVoltage,
                            onClick = { viewModel.setShowVoltage(!s.showVoltage) }
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                        TickSettingItem(
                            title = "Wattage",
                            subtitle = "Show power consumption",
                            icon = Icons.Default.FlashOn,
                            selected = s.showWattage,
                            onClick = { viewModel.setShowWattage(!s.showWattage) }
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                        TickSettingItem(
                            title = "Current",
                            subtitle = "Show charging current (mA)",
                            icon = Icons.Default.ElectricBolt,
                            selected = s.showCurrent,
                            onClick = { viewModel.setShowCurrent(!s.showCurrent) }
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                        TickSettingItem(
                            title = "Est. Time",
                            subtitle = "Show estimated time to full",
                            icon = Icons.Default.HourglassTop,
                            selected = s.showEstimatedTime,
                            onClick = { viewModel.setShowEstimatedTime(!s.showEstimatedTime) }
                        )
                    }
                }
            }
        }
    }

    if (showTimingSheet) {
        ModalBottomSheet(
            onDismissRequest = { showTimingSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Text("Dim & Hide Timings", style = MaterialTheme.typography.titleLarge)
                TimingSliderItem(
                    title = "Dim Screen After",
                    subtitle = "Reduce brightness after inactivity",
                    icon = Icons.Default.BrightnessLow,
                    value = s.dimAfterSeconds,
                    range = 5..120,
                    unit = "s",
                    onValueChange = { viewModel.setDimAfterSeconds(it) }
                )
                TimingSliderItem(
                    title = "Hide Screen After",
                    subtitle = "Turn off display after inactivity",
                    icon = Icons.Default.VisibilityOff,
                    value = s.hideAfterSeconds,
                    range = 10..300,
                    unit = "s",
                    onValueChange = { viewModel.setHideAfterSeconds(it) }
                )
            }
        }
    }

    if (showDateFormatSheet) {
        ModalBottomSheet(
            onDismissRequest = { showDateFormatSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 32.dp)
            ) {
                Text(
                    text = "Date Format",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = "Select how the date appears on your display",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        listOf(
                            "EEE, dd MMM",
                            "EEE, dd MMM yyyy",
                            "EEEE, MMMM d",
                            "EEEE, d MMMM yyyy",
                            "MMM d, yyyy",
                            "d MMM yyyy",
                            "dd/MM/yyyy",
                        ).forEachIndexed { index, pattern ->
                            val isSelected = s.dateFormat == pattern
                            if (index != 0)
                                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.setDateFormat(pattern)
                                        showDateFormatSheet = false
                                    }
                                    .padding(horizontal = 16.dp, vertical = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = previewDate(pattern),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1f)
                                )
                                if (isSelected) {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ── Reusable components  ───────────────────────


@Composable
private fun SettingsNavItem(
    title: String,
    subtitle: String?,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null)
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            if (subtitle != null)
                Text(subtitle, style = MaterialTheme.typography.bodySmall)
        }
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
    }
}

@Composable
fun SettingSwitchItem(
    title: String,
    subtitle: String?,
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
        Icon(icon, contentDescription = null)
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            if (subtitle != null)
                Text(subtitle, style = MaterialTheme.typography.bodySmall)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun TickSettingItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint =
//                if (selected) MaterialTheme.colorScheme.primary else
                    MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                title,
                style = MaterialTheme.typography.titleMedium,
                color =
//                    if (selected) MaterialTheme.colorScheme.primary else
                    MaterialTheme.colorScheme.onSurface
            )
            Text(
                subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        if (selected) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .padding(4.dp)
                        .fillMaxSize()
                )
            }
        }
    }
}

@Composable
private fun TimingSliderItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    value: Int,
    range: IntRange,
    unit: String,
    onValueChange: (Int) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Text(subtitle, style = MaterialTheme.typography.bodySmall)
            }
            Text(
                text = "$value$unit",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Slider(
            value = value.toFloat(),
            onValueChange = { onValueChange(it.toInt()) },
            valueRange = range.first.toFloat()..range.last.toFloat(),
            steps = 0,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
        )
    }
}