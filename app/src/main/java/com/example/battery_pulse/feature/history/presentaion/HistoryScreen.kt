package com.example.battery_pulse.feature.history.presentaion

import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.battery_pulse.core.ui.theme.primaryDark
import com.example.battery_pulse.feature.history.domain.model.ChargingSession
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    navController: NavController,
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {


        when (val state = uiState) {
            is HistoryUiState.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            is HistoryUiState.Empty -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.BatteryChargingFull,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            "No charging sessions yet",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            "Plug in your charger to start tracking",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            is HistoryUiState.Error -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Error: ${state.message}", color = MaterialTheme.colorScheme.error)
                }
            }

            is HistoryUiState.Success -> {
                ChargingSessionList(
                    sessions = state.sessions,
                    onDelete = { session ->
                        viewModel.deleteSession(session.id)
                        scope.launch {
                            val result = snackbarHostState.showSnackbar(
                                message = "Session deleted",
                                actionLabel = "Undo",
                                duration = SnackbarDuration.Long
                            )
                            Log.d("HistoryScreen", "Snackbar result: $result")
                            if (result == SnackbarResult.ActionPerformed) {
                                viewModel.undoDelete(session)
                            }
                        }
                    }
                )
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun ChargingSessionList(
    sessions: List<ChargingSession>,
    onDelete: (ChargingSession) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Charging History",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }
        items(sessions, key = { it.id }) { session ->
            SwipeToDeleteContainer(
                onDelete = { onDelete(session) }
            ) {
                ChargingSessionItem(session)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeToDeleteContainer(
    onDelete: () -> Unit,
    content: @Composable () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                onDelete()
                true
            } else false
        },
        positionalThreshold = { it * 0.4f }
    )

    LaunchedEffect(dismissState.currentValue) {
        if (dismissState.currentValue == SwipeToDismissBoxValue.EndToStart) {
            dismissState.snapTo(SwipeToDismissBoxValue.Settled)
        }
    }

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            val color by animateColorAsState(
                targetValue = if (dismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart)
                    MaterialTheme.colorScheme.errorContainer
                else
                    MaterialTheme.colorScheme.surface,
                animationSpec = tween(300),
                label = "swipe_bg_color"
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(20.dp))
                    .background(color),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(end = 20.dp)
                )
            }
        }
    ) {
        content()
    }
}

@Composable
private fun ChargingSessionItem(session: ChargingSession) {
    val colorScheme = MaterialTheme.colorScheme

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {

            // ── Row 1: Date (left) + Gained badge (right) ─────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formatDate(session.startTime),
                    style = MaterialTheme.typography.labelMedium,
                    color = colorScheme.onSurfaceVariant
                )
                Surface(
                    shape = RoundedCornerShape(50),
                    color = colorScheme.primaryContainer
                ) {
                    Text(
                        text = "+${session.gainedPercent}%",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            // ── Row 2: "Charged for Xh Ym" label ─────────────────────────
            Text(
                text = "Charged for ${formatDuration(session.durationMinutes)}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = colorScheme.onSurface
            )

            Spacer(Modifier.height(12.dp))

            // ── Battery bar ───────────────────────────────────────────────
            BatteryRangeBar(
                startPercent = session.startBattery,
                endPercent = session.endBattery,
                colorScheme = colorScheme
            )

            Spacer(Modifier.height(12.dp))

            // ── Row 3: Plug in / Plug out ──────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Plug In
                Column {
                    Text(
                        text = "Plug in",
                        style = MaterialTheme.typography.labelSmall,
                        color = colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = formatTime(session.startTime),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = colorScheme.onSurface
                    )
                }

                // Divider dot
                Text(
                    text = "·",
                    style = MaterialTheme.typography.bodyLarge,
                    color = colorScheme.outlineVariant,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )

                // Plug Out
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Plug out",
                        style = MaterialTheme.typography.labelSmall,
                        color = colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = formatTime(session.endTime),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = colorScheme.onSurface
                    )
                }
            }
        }
    }
}

/**
 * Battery bar with three zones:
 *  • 0% → startPercent  : dim "pre-charge" fill (surfaceContainerHighest)
 *  • startPercent → endPercent : gradient primary → tertiary  (charged range)
 *  • endPercent → 100%  : empty track (surfaceContainerHighest)
 *
 * Labels: start% and end% float above the bar at their exact x-position.
 */
/**
 * Three zones:
 *  0 → startPercent   : dim primary tint  (pre-charge)
 *  startPercent → endPercent : gradient primary → tertiary  (charged)
 *  endPercent → 100   : empty track
 *
 *  Labels: startPercent fixed to far LEFT, endPercent fixed to far RIGHT.
 */
@Composable
private fun BatteryRangeBar(
    startPercent: Int,
    endPercent: Int,
    colorScheme: ColorScheme
) {
    val startFraction = startPercent / 100f
    val endFraction = endPercent / 100f
    val gapFraction = endFraction - startFraction
    val labelsOverlap = gapFraction < 0.20f

    Column {

        // ── Labels above bar ──────────────────────────────────────────────
        if (labelsOverlap) {
            Text(
                text = "${startPercent}% → ${endPercent}%",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = colorScheme.onSurface,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        } else {
            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                val tw = maxWidth

                Text(
                    text = "${startPercent}%",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = colorScheme.onSurface,
                    modifier = Modifier.offset(
                        x = (tw * startFraction).coerceAtMost(tw - 36.dp)
                    )
                )

                Text(
                    text = "${endPercent}%",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = colorScheme.onSurface,
                    modifier = Modifier.offset(
                        x = (tw * endFraction - 14.dp).coerceIn(0.dp, tw - 28.dp)
                    )
                )
            }
        }

        Spacer(Modifier.height(4.dp))

        // ── Bar ───────────────────────────────────────────────────────────
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .height(20.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(colorScheme.surfaceContainerHighest)
        ) {
            val tw = maxWidth
            val cornerPx = 6.dp

            // Zone 1 — pre-charge (0 → start%)
            if (startFraction > 0f) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(tw * startFraction)
                        .background(
                            colorScheme.primary.copy(
                                red = (colorScheme.primary.red * 1.3f).coerceAtMost(1f),
                                green = (colorScheme.primary.green * 1.3f).coerceAtMost(1f),
                                blue = (colorScheme.primary.blue * 1.3f).coerceAtMost(1f)
                            )
                        )
                )
            }

            // Zone 2 — charged (start% → end%)
            if (endFraction > startFraction) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .offset(x = tw * startFraction)
                        .width(tw * gapFraction)
                        .clip(
                            RoundedCornerShape(
                                topStart = 0.dp,
                                bottomStart = 0.dp,
                                topEnd = cornerPx,
                                bottomEnd = cornerPx
                            )
                        )
                        .background(colorScheme.primary.copy(0.8f))
                )
            }
        }

        Spacer(Modifier.height(4.dp))

        // ── Scale anchors ─────────────────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "0%",
                style = MaterialTheme.typography.labelSmall,
                color = colorScheme.outlineVariant
            )
            Text(
                "100%",
                style = MaterialTheme.typography.labelSmall,
                color = colorScheme.outlineVariant
            )
        }
    }
}
// ── Formatters ────────────────────────────────────────────────────────────────

private fun formatDate(ms: Long): String =
    SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault()).format(Date(ms))

private fun formatTime(ms: Long): String =
    SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(ms))

private fun formatDuration(minutes: Long): String {
    val h = minutes / 60
    val m = minutes % 60
    return if (h > 0) "${h}h ${m}m" else "${m}m"
}
