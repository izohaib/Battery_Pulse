package com.example.battery_pulse.feature.on_display.presentation.fullScreenIntentActivity

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.VerticalAlignmentLine
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.battery_pulse.core.utils.TorchHelper
import com.example.battery_pulse.core.utils.Utils
import com.example.battery_pulse.feature.battery.domain.models.BatteryInfo
import com.example.battery_pulse.feature.on_display.data.model.OnDisplaySettings
import com.example.battery_pulse.feature.on_display.presentation.components.Speedometer
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

// ─────────────────────────────────────────────────────────────────────────────
// Private colours
// ─────────────────────────────────────────────────────────────────────────────

private val CardBackground = Color(0xFF1C1C1E)
private val SubtextColor = Color(0xFF9E9E9E)
private val ActionIconColor = Color(0xFFE0E0E0)

// ─────────────────────────────────────────────────────────────────────────────
// Helpers
// ─────────────────────────────────────────────────────────────────────────────

private fun formattedTime(use24Hour: Boolean): String {
    val pattern = if (use24Hour) "HH:mm" else "hh:mm a"
    return SimpleDateFormat(pattern, Locale.getDefault()).format(Date())
}

private fun formattedDate(format: String): String =
    SimpleDateFormat(format, Locale.getDefault()).format(Date())

private fun Int.toEtaString(): String =
    if (this >= 60) "${this / 60}h ${this % 60}m" else "${this}m"

// ─────────────────────────────────────────────────────────────────────────────
// Screen
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun ChargingDisplayScreen(
    settings: OnDisplaySettings,
    batteryInfo: BatteryInfo,
) {

    val context = LocalContext.current
    val activity: ChargingDisplayActivity? = context as? ChargingDisplayActivity

    var lastTouchTime by remember {
        mutableLongStateOf(System.currentTimeMillis())
    }
    var isDimmed by remember { mutableStateOf(false) }
    var isHide by remember { mutableStateOf(false) }

    val time by produceState(
        initialValue = formattedTime(settings.is24HourFormat),
        key1 = settings.is24HourFormat,
    ) {
        while (true) {
            delay(1_000L)
            value = formattedTime(settings.is24HourFormat)
        }
    }

    val date by produceState(
        initialValue = formattedDate(settings.dateFormat),
        key1 = settings.dateFormat
    ) {
        while (true) {
            delay(60_000L)
            value = formattedDate(settings.dateFormat)
        }
    }

    val tiles = buildInfoTiles(settings, batteryInfo)
    val isFlashOn by TorchHelper.isFlashOn.collectAsState()

    // only start timer if inside real ChargingDisplayActivity
    if(activity != null && !settings.alwaysOnDisplay) {
        LaunchedEffect(settings.dimAfterSeconds, settings.hideAfterSeconds) {
            while (true) {
                delay(1_000L)
                val idleSeconds = (System.currentTimeMillis() - lastTouchTime) / 1000
                Log.d(
                    "DimHide",
                    "idleSeconds=$idleSeconds | dim=${settings.dimAfterSeconds} | hide=${settings.hideAfterSeconds} | AOD=${settings.alwaysOnDisplay}| isDimmed=$isDimmed | isHide=$isHide"
                )

                if (idleSeconds >= settings.hideAfterSeconds && !isHide) {
                    isHide = true
                    isDimmed = true
                    Log.d("DimHide", "→ hideScreen() called")

                } else if (idleSeconds >= settings.dimAfterSeconds && !isDimmed) {
                    isDimmed = true
                    Log.d("DimHide", "→ dimBrightness() called")
                    activity.dimBrightness(0.08f)
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .pointerInput(Unit) {
                detectTapGestures {
                    isDimmed = false
                    isHide = false
                    lastTouchTime = System.currentTimeMillis()
                    activity?.restoreBrightness()
                    val currentTime =
                        SimpleDateFormat("HH:mm:ss", Locale.FRENCH).format(Date(lastTouchTime))
                    Log.d("lastTouchTime", currentTime)
                    Log.d("DimHide", "→ screen touched, everything reset")
                }
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 28.dp, end = 28.dp, top = 52.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {


            // ── Top — Clock + Date ─────────────────────────────────────────
            ClockSection(
                visible = !settings.hideClock,
                time = time,
                date = date
            )

            // ── Middle — Speedometer ───────────────────────────────────────
            Speedometer(
                percent = batteryInfo.percent,
                isCharging = batteryInfo.isCharging,
                accentColor = Color(settings.speedometerColor),
                size = 220.dp
            )

            // ── Bottom — Info Grid + Action Row ────────────────────────────
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (tiles.isNotEmpty()) {
                    InfoTileGrid(tiles = tiles)
                }
                BottomActionRow(
                    displaySettings = settings,
                    onTorchClick = {
                        TorchHelper.toggle(context)
                    },
                    onCameraClick = {
                        Utils.launchCamera(context = context)
                    },

                    )
            }


        }

        if (isHide) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
                    .pointerInput(Unit) {
                        detectTapGestures {

                            isDimmed = false
                            isHide = false
                            lastTouchTime = System.currentTimeMillis()
                            activity?.restoreBrightness()
                            Log.d("DimHide", "→ overlay touched, everything reset")
                        }
                    }
            )
        }

    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Clock section
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ClockSection(
    visible: Boolean,
    time: String,
    date: String
) {
    if (visible) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            val parts = time.split(" ")
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = parts[0],
                    fontSize = 64.sp,
                    fontWeight = FontWeight.Thin,
                    color = Color.White,
                    letterSpacing = 2.sp
                )
                if(parts.size > 1) {
                    Text(
                        text = parts[1].uppercase(),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Light,
                        color = SubtextColor,
                        modifier = Modifier.padding(start = 6.dp, bottom = 10.dp)
                    )
                }
            }
            Spacer(Modifier.height(4.dp))
            Text(
                text = date,
                fontSize = 14.sp,
                color = SubtextColor,
                letterSpacing = 1.sp
            )
        }
    } else {
        // Keeps SpaceBetween layout stable when clock is hidden
        Spacer(Modifier.height(88.dp))
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Info tile grid  (wraps into 2 rows when > 4 tiles)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun InfoTileGrid(tiles: List<InfoTileData>) {
    // 1–4 tiles → single row; 5+ → split evenly across 2 rows
    val columns = when {
        tiles.size <= 4 -> tiles.size
        else -> (tiles.size + 1) / 2   // ceil(n/2)
    }
    val rows = tiles.chunked(columns)

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        rows.forEach { rowTiles ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowTiles.forEach { tile ->
                    InfoTile(modifier = Modifier.weight(1f), tile = tile)
                }
                // Pad incomplete last row so all tiles stay equal-width
                repeat(columns - rowTiles.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun InfoTile(
    modifier: Modifier = Modifier,
    tile: InfoTileData
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = CardBackground
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = tile.value,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                maxLines = 1
            )
            Text(
                text = tile.label,
                fontSize = 10.sp,
                color = SubtextColor,
                maxLines = 1
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Bottom action row — torch bottom-left, camera bottom-right
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun BottomActionRow(
    displaySettings: OnDisplaySettings,
    onTorchClick: () -> Unit,
    onCameraClick: () -> Unit
) {

    val isFlashOn by TorchHelper.isFlashOn.collectAsState()
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (displaySettings.showTorch) {
            ActionButton(
                icon = if (isFlashOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                contentDescription = "Toggle torch",
                onLongClick = onTorchClick
            )
        } else {
            Spacer(Modifier.size(48.dp))
        }

        if (displaySettings.showCamera) {
            ActionButton(
                icon = Icons.Default.CameraAlt,
                contentDescription = "Open camera",
                onLongClick = onCameraClick
            )
        } else {
            Spacer(Modifier.size(48.dp))
        }
    }
}

@Composable
private fun ActionButton(
    icon: ImageVector,
    contentDescription: String,
    onLongClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = CardBackground
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(48.dp)
                .combinedClickable(
                    onLongClick = onLongClick,
                    onClick = {}
                )
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = ActionIconColor,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Tile data + builder
// ─────────────────────────────────────────────────────────────────────────────

private data class InfoTileData(val label: String, val value: String)

private fun buildInfoTiles(
    settings: OnDisplaySettings,
    batteryInfo: BatteryInfo
): List<InfoTileData> = buildList {

    if (settings.showTemperature) {
        add(InfoTileData("Temp", "${batteryInfo.temperatureCelsius}°C"))
    }
    if (settings.showVoltage) {
        add(InfoTileData("Voltage", "${batteryInfo.voltageMilliVolts} mV"))
    }
    if (settings.showWattage) {
        add(InfoTileData("Power", "${"%.1f".format(batteryInfo.chargingWatts)} W"))
    }
    if (settings.showCurrent) {
        add(InfoTileData("Current", "${batteryInfo.currentMicroAmps} mA"))
    }
    if (settings.showEstimatedTime && batteryInfo.timeToFullMinutes != null) {
        add(InfoTileData("ETA", batteryInfo.timeToFullMinutes.toEtaString()))
    }
}