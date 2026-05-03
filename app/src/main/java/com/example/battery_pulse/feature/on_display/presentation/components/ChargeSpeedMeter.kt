package com.example.battery_pulse.feature.on_display.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Speedometer(
    percent: Int,
    isCharging: Boolean,
    accentColor: Color,
    size: Dp = 140.dp,
    modifier: Modifier = Modifier
) {
    val sweepAngle = 240f
    val startAngle = 150f
    val fraction = percent / 100f

    Box(contentAlignment = Alignment.Center, modifier = modifier) {
        Canvas(modifier = Modifier.size(size)) {
            val strokeWidth = this.size.width * 0.09f
            val inset = strokeWidth / 2f
            val arcSize = Size(this.size.width - inset * 2, this.size.height - inset * 2)
            val topLeft = Offset(inset, inset)

            drawArc(
                color = Color.White.copy(alpha = 0.08f),
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
            drawArc(
                color = accentColor,
                startAngle = startAngle,
                sweepAngle = sweepAngle * fraction,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            val needleAngleRad = Math.toRadians((startAngle + sweepAngle * fraction).toDouble())
            val radius = (this.size.width / 2f) - inset
            val cx = this.size.width / 2f
            val cy = this.size.height / 2f

//            drawLine(
//                color = Color.White.copy(alpha = 0.9f),
//                start = Offset(cx, cy),
//                end = Offset(
//                    x = (cx + radius * cos(needleAngleRad)).toFloat(),
//                    y = (cy + radius * sin(needleAngleRad)).toFloat()
//                ),
//                strokeWidth = strokeWidth * 0.25f,
//                cap = StrokeCap.Round
//            )
//            drawCircle(color = Color.White, radius = strokeWidth * 0.35f, center = Offset(cx, cy))
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("$percent%", fontSize = (size.value * 0.16f).sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text(
                text = if (isCharging) "Charging" else "Not Charging",
                fontSize = (size.value * 0.09f).sp,
                color = Color.Gray
            )
        }
    }
}