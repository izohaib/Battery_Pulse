package com.example.battery_pulse.feature.battery.presentaion.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import com.example.battery_pulse.R
import com.example.battery_pulse.core.ui.theme.glow
import kotlin.math.abs

@Composable
fun HeroSection(
    percent: Int,
    chargingWatts: Float,
    isCharging: Boolean,
) {
    var isAlwaysOnDisplay by remember { mutableStateOf(true) }
    val isDark = isSystemInDarkTheme()

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 360.dp, max = 420.dp),
        shape = RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp),
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clipToBounds()
        ) {
            Image(
                painter = if (isDark) painterResource(R.drawable.charging_dark) else painterResource(R.drawable.charging_light),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(260.dp)
                    .offset(x = if (isDark) 110.dp else 100.dp, y = (-20).dp)
                    .rotate(10f),
                contentScale = ContentScale.Crop,
                alpha = 0.85f
            )

            Column(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .fillMaxWidth(0.70f)
                    .padding(start = 24.dp, bottom = 24.dp, top = 24.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                WattBadge(chargingWatts, isCharging)
                BatteryPercentage(percent)
                ChargingStatus(isCharging)
                BatterySegments(percent)
                AlwaysOnToggle(
                    isOn = isAlwaysOnDisplay,
                    onToggle = { isAlwaysOnDisplay = it }
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HeroSectionPreview() {
    HeroSection(percent = 78, chargingWatts = 22.5f, isCharging = true)
}

@Composable
private fun WattBadge(chargingWatts: Float, isCharging: Boolean) {
    val absWatts = abs(chargingWatts)
    val display = if (absWatts < 1f)
        "${"%.0f".format(chargingWatts * 1000)} mW"
    else
        "${"%.1f".format(chargingWatts)} W"

    Row(
        verticalAlignment = Alignment.CenterVertically,  // fixes alignment
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        if (isCharging) {
            Icon(
                imageVector = Icons.Filled.Bolt,
                contentDescription = "Charging",
                modifier = Modifier
                    .size(28.dp)
                    .drawBehind {
                        drawIntoCanvas { canvas ->
                            val paint = Paint().asFrameworkPaint().apply {
                                isAntiAlias = true
                                color = android.graphics.Color.TRANSPARENT
                                setShadowLayer(
                                    6f,   // blur radius — increase for more spread
                                    0f,
                                    0f,
                                    android.graphics.Color.argb(40, 255, 193, 7) // amber glow
                                )
                            }
                            canvas.nativeCanvas.drawCircle(
                                size.width / 2,
                                size.height / 2,
                                size.minDimension / 2,
                                paint
                            )
                        }
                    },
                tint = MaterialTheme.colorScheme.glow
            )
        }

        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surfaceContainerHigh
        ) {
            Text(
                text = display,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun BatteryPercentage(percent: Int) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = "$percent",
                fontSize = 72.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                lineHeight = 72.sp
            )
            Text(
                text = "%",
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 10.dp, start = 2.dp)
            )
        }
        Text(
            text = "Battery Level",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ChargingStatus(isCharging: Boolean) {
    Text(
        text = if (isCharging) "Charging" else "Not Charging",
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground
    )
}

@Composable
private fun BatterySegments(percent: Int) {
    val filledCount = (percent / 10).coerceIn(0, 10)

    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        repeat(10) { index ->
            Box(
                modifier = Modifier
                    .width(16.dp)
                    .height(28.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        if (index < filledCount) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surfaceContainerHighest
                    )
            )
        }
    }
}

@Composable
private fun AlwaysOnToggle(isOn: Boolean, onToggle: (Boolean) -> Unit) {
    Column(modifier = Modifier.padding(top = 5.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Always-On Display",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        TogglePill(isOn = isOn, onToggle = onToggle)
    }
}

@Composable
private fun TogglePill(isOn: Boolean, onToggle: (Boolean) -> Unit) {
    Surface(
        shape = RoundedCornerShape(99.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh
    ) {
        Row(modifier = Modifier.padding(4.dp)) {
            ToggleOption(label = "Off", selected = !isOn, onClick = { onToggle(false) })
            ToggleOption(label = "On", selected = isOn, onClick = { onToggle(true) })
        }
    }
}

@Composable
private fun ToggleOption(label: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(99.dp))
            .background(
                if (selected) MaterialTheme.colorScheme.primaryContainer
                else androidx.compose.ui.graphics.Color.Transparent
            )
            .clickable { onClick() }
            .padding(horizontal = 18.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (selected)
                MaterialTheme.colorScheme.onPrimaryContainer
            else
                MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}