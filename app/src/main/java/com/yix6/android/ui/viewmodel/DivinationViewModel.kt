package com.yix6.android.ui.viewmodel

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yix6.android.domain.model.CoinFace
import com.yix6.android.domain.model.Throw
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.sqrt
import kotlin.random.Random

/** Divination mode chosen on the Home screen. */
enum class DivinationMode { SHAKE, TAP }

data class DivinationState(
    val mode: DivinationMode = DivinationMode.SHAKE,
    /** Completed throws so far (0..5 entries). */
    val throws: List<Throw> = emptyList(),
    /** Coins for the current round being set up (Tap mode: 3 nullable faces). */
    val currentCoins: List<CoinFace?> = listOf(null, null, null),
    /** True while the shake cooldown is active (prevents double-shake). */
    val shakeCooldown: Boolean = false,
    /** True if all 6 rounds are complete. */
    val isComplete: Boolean = false,
    /** True while the shake animation/feedback is playing. */
    val shakeDetected: Boolean = false,
)

class DivinationViewModel : ViewModel() {

    private val _state = MutableStateFlow(DivinationState())
    val state: StateFlow<DivinationState> = _state.asStateFlow()

    // ── Shake detection ──────────────────────────────────────────────────────

    private val SHAKE_THRESHOLD_G = 2.7f
    private val COOLDOWN_MS = 1500L
    private val SHAKE_ANIMATION_DELAY_MS = 600L
    private var lastShakeMs = 0L

    val shakeListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            if (event.sensor.type != Sensor.TYPE_ACCELEROMETER) return
            val s = _state.value
            if (s.mode != DivinationMode.SHAKE || s.isComplete || s.shakeCooldown) return

            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]
            val gForce = sqrt(x * x + y * y + z * z) / SensorManager.GRAVITY_EARTH

            val now = System.currentTimeMillis()
            if (gForce > SHAKE_THRESHOLD_G && (now - lastShakeMs) > COOLDOWN_MS) {
                lastShakeMs = now
                onShakeDetected()
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
    }

    private fun onShakeDetected() {
        val current = _state.value
        if (current.isComplete || current.throws.size >= 6) return

        _state.update { it.copy(shakeCooldown = true, shakeDetected = true) }

        val coins = randomThrow()
        viewModelScope.launch {
            // Brief delay to show shake animation
            kotlinx.coroutines.delay(SHAKE_ANIMATION_DELAY_MS)
            val newThrows = _state.value.throws + coins
            _state.update {
                it.copy(
                    throws = newThrows,
                    shakeCooldown = false,
                    shakeDetected = false,
                    isComplete = newThrows.size == 6,
                )
            }
        }
    }

    // ── Tap mode ─────────────────────────────────────────────────────────────

    /** Toggle a coin face in tap mode (cycles HEADS ↔ TAILS, null → HEADS). */
    fun toggleCoin(index: Int) {
        _state.update { s ->
            val updated = s.currentCoins.toMutableList()
            updated[index] = when (updated[index]) {
                null -> CoinFace.HEADS
                CoinFace.HEADS -> CoinFace.TAILS
                CoinFace.TAILS -> CoinFace.HEADS
            }
            s.copy(currentCoins = updated)
        }
    }

    /** Confirm the current tap-mode round (all 3 coins must be set). */
    fun confirmRound() {
        val s = _state.value
        val faces = s.currentCoins
        if (faces.any { it == null }) return

        val t = Throw(faces.filterNotNull())
        val newThrows = s.throws + t
        _state.update {
            it.copy(
                throws = newThrows,
                currentCoins = listOf(null, null, null),
                isComplete = newThrows.size == 6,
            )
        }
    }

    // ── Mode switching ────────────────────────────────────────────────────────

    fun setMode(mode: DivinationMode) {
        _state.update { it.copy(mode = mode) }
    }

    // ── Undo / Reset ──────────────────────────────────────────────────────────

    fun undo() {
        _state.update { s ->
            if (s.throws.isEmpty()) return@update s
            s.copy(
                throws = s.throws.dropLast(1),
                currentCoins = listOf(null, null, null),
                isComplete = false,
                shakeCooldown = false,
            )
        }
    }

    fun reset() {
        _state.update {
            DivinationState(mode = it.mode)
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun randomThrow(): Throw =
        Throw(List(3) { if (Random.nextBoolean()) CoinFace.HEADS else CoinFace.TAILS })
}
