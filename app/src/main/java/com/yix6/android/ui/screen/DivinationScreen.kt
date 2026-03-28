package com.yix6.android.ui.screen

import android.content.Context
import android.hardware.SensorManager
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yix6.android.domain.model.CoinFace
import com.yix6.android.domain.model.SixThrows
import com.yix6.android.ui.viewmodel.DivinationMode
import com.yix6.android.ui.viewmodel.DivinationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DivinationScreen(
    initialMode: DivinationMode,
    onComplete: (SixThrows) -> Unit,
    onBack: () -> Unit,
    viewModel: DivinationViewModel = viewModel(),
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()

    // Set initial mode
    LaunchedEffect(initialMode) {
        viewModel.setMode(initialMode)
    }

    // Register / unregister accelerometer for shake mode
    DisposableEffect(state.mode) {
        if (state.mode == DivinationMode.SHAKE) {
            val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
            val accelerometer = sensorManager.getDefaultSensor(android.hardware.Sensor.TYPE_ACCELEROMETER)
            sensorManager.registerListener(
                viewModel.shakeListener,
                accelerometer,
                SensorManager.SENSOR_DELAY_GAME,
            )
            onDispose {
                sensorManager.unregisterListener(viewModel.shakeListener)
            }
        } else {
            onDispose { }
        }
    }

    // Vibrate on shake detection
    LaunchedEffect(state.shakeDetected) {
        if (state.shakeDetected) {
            @Suppress("DEPRECATION")
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            vibrator.vibrate(VibrationEffect.createOneShot(120, VibrationEffect.DEFAULT_AMPLITUDE))
        }
    }

    // Navigate when complete
    LaunchedEffect(state.isComplete) {
        if (state.isComplete && state.throws.size == 6) {
            onComplete(SixThrows(state.throws))
        }
    }

    val shakeScale by animateFloatAsState(
        targetValue = if (state.shakeDetected) 1.3f else 1f,
        animationSpec = tween(300),
        label = "shakeScale",
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Round ${state.throws.size + 1} of 6") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.undo() }, enabled = state.throws.isNotEmpty()) {
                        Icon(Icons.Default.Undo, contentDescription = "Undo")
                    }
                    IconButton(onClick = { viewModel.reset() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Reset")
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
        ) {
            // Progress bar
            LinearProgressIndicator(
                progress = { state.throws.size / 6f },
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Completed throws
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                itemsIndexed(state.throws) { index, t ->
                    ThrowRow(round = index + 1, throwResult = t)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Current round controls
            if (!state.isComplete) {
                when (state.mode) {
                    DivinationMode.SHAKE -> ShakeModePanel(
                        shakeScale = shakeScale,
                        shakeCooldown = state.shakeCooldown,
                    )

                    DivinationMode.TAP -> TapModePanel(
                        coins = state.currentCoins,
                        onToggle = { viewModel.toggleCoin(it) },
                        onConfirm = { viewModel.confirmRound() },
                    )
                }
            }
        }
    }
}

@Composable
private fun ThrowRow(round: Int, throwResult: com.yix6.android.domain.model.Throw) {
    val valueLabel = when (throwResult.value) {
        6 -> "6 – Old Yin ●"
        7 -> "7 – Young Yang —"
        8 -> "8 – Young Yin - -"
        9 -> "9 – Old Yang ○"
        else -> "${throwResult.value}"
    }
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Line $round",
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.width(60.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
            throwResult.coins.forEach { face ->
                CoinBadge(face = face)
                Spacer(modifier = Modifier.width(4.dp))
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = valueLabel,
                style = MaterialTheme.typography.bodySmall,
                color = if (throwResult.isChanging)
                    MaterialTheme.colorScheme.error
                else
                    MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun CoinBadge(face: CoinFace?) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(
                when (face) {
                    CoinFace.HEADS -> MaterialTheme.colorScheme.primary
                    CoinFace.TAILS -> MaterialTheme.colorScheme.secondary
                    null -> MaterialTheme.colorScheme.surfaceVariant
                }
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = face?.display() ?: "?",
            color = when (face) {
                null -> MaterialTheme.colorScheme.onSurfaceVariant
                else -> Color.White
            },
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
        )
    }
}

@Composable
private fun ShakeModePanel(shakeScale: Float, shakeCooldown: Boolean) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "🪙",
            fontSize = (64 * shakeScale).sp,
            modifier = Modifier.scale(shakeScale),
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = if (shakeCooldown) "Casting…" else "Shake your phone to cast coins!",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = if (shakeCooldown)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun TapModePanel(
    coins: List<CoinFace?>,
    onToggle: (Int) -> Unit,
    onConfirm: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Tap each coin to set HEADS or TAILS",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            coins.forEachIndexed { index, face ->
                TapCoinButton(face = face, onClick = { onToggle(index) })
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onConfirm,
            enabled = coins.none { it == null },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Confirm Round")
        }
    }
}

@Composable
private fun TapCoinButton(face: CoinFace?, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(72.dp)
            .clip(CircleShape)
            .background(
                when (face) {
                    CoinFace.HEADS -> MaterialTheme.colorScheme.primary
                    CoinFace.TAILS -> MaterialTheme.colorScheme.secondary
                    null -> MaterialTheme.colorScheme.surfaceVariant
                }
            )
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = CircleShape,
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = when (face) {
                    CoinFace.HEADS -> "H"
                    CoinFace.TAILS -> "T"
                    null -> "?"
                },
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = when (face) {
                    null -> MaterialTheme.colorScheme.onSurfaceVariant
                    else -> Color.White
                },
            )
            Text(
                text = when (face) {
                    CoinFace.HEADS -> "Heads"
                    CoinFace.TAILS -> "Tails"
                    null -> "Tap"
                },
                fontSize = 10.sp,
                color = when (face) {
                    null -> MaterialTheme.colorScheme.onSurfaceVariant
                    else -> Color.White
                },
            )
        }
    }
}
