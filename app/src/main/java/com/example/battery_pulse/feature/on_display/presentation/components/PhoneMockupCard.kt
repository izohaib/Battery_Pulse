package com.example.battery_pulse.feature.on_display.presentation.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.battery_pulse.feature.battery.domain.models.BatteryInfo
import com.example.battery_pulse.feature.on_display.data.model.OnDisplaySettings
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints
import com.example.battery_pulse.feature.on_display.presentation.fullScreenIntentActivity.ChargingDisplayScreen

private const val PREVIEW_SCALE = 0.35f

@Composable
fun PhoneMockupCard(
    settings: OnDisplaySettings,
    batteryInfo: BatteryInfo,
) {
    val outerShape = RoundedCornerShape(26.dp)
    val middleShape = RoundedCornerShape(24.dp)
    val innerShape  = RoundedCornerShape(22.dp)

    val aodOn = settings.alwaysOnDisplay

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.width(120.dp).height(220.dp)
    ) {
        // ── Outer border: primary when AoD ON, outline when OFF ──────────
        Box(
            modifier = Modifier
                .width(120.dp)
                .height(220.dp)
                .border(
                    width = 6.dp,
                    color = if (aodOn) MaterialTheme.colorScheme.primary
                    else       MaterialTheme.colorScheme.outline,
                    shape = outerShape
                )
                .padding(3.dp)
                // ── Inner accent border: only visible when AoD ON ────────
                .then(
                    if (aodOn)
                        Modifier
                            .border(
                                width = 3.dp,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.45f),
                                shape = middleShape
                            )
                            .padding(2.dp)
                    else
                        Modifier.padding(2.dp)
                )
                .clip(innerShape)
        ) {
            Layout(
                content = {
                    key(settings) {
                        ChargingDisplayScreen(
                            settings = settings,
                            batteryInfo = batteryInfo
                        )
                    }
                }
            ) { measurables, constraints ->
                val scaledConstraints = Constraints(
                    minWidth  = (constraints.minWidth  / PREVIEW_SCALE).toInt(),
                    maxWidth  = (constraints.maxWidth  / PREVIEW_SCALE).toInt(),
                    minHeight = (constraints.minHeight / PREVIEW_SCALE).toInt(),
                    maxHeight = (constraints.maxHeight / PREVIEW_SCALE).toInt()
                )
                val placeable = measurables.first().measure(scaledConstraints)
                layout(constraints.maxWidth, constraints.maxHeight) {
                    placeable.placeWithLayer(
                        x = -(placeable.width  * (1 - PREVIEW_SCALE) / 2).toInt(),
                        y = -(placeable.height * (1 - PREVIEW_SCALE) / 2).toInt()
                    ) {
                        scaleX = PREVIEW_SCALE
                        scaleY = PREVIEW_SCALE
                    }
                }
            }
        }
    }
}