package com.yix6.android.domain.model

/**
 * Represents the complete six rounds of coin throws for one I Ching reading.
 * Lines are built from bottom (index 0) to top (index 5).
 */
data class SixThrows(val throws: List<Throw>) {
    init {
        require(throws.size == 6) { "SixThrows must contain exactly 6 throws." }
    }

    val isComplete: Boolean get() = throws.size == 6

    /** Returns which line indices (0-based, bottom=0) are changing lines. */
    val changingLineIndices: List<Int>
        get() = throws.mapIndexedNotNull { index, t -> if (t.isChanging) index else null }
}
