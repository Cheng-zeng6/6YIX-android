package com.yix6.android.domain.model

/**
 * Represents a single round: three coins thrown together.
 *
 * The numeric value (6–9) is computed from the coin faces:
 *  HEADS = 3, TAILS = 2 → sum of three coins ranges from 6 to 9.
 */
data class Throw(val coins: List<CoinFace>) {
    init {
        require(coins.size == 3) { "A Throw must have exactly 3 coins." }
    }

    /** Numeric value: HEADS=3, TAILS=2 → sum is 6..9 */
    val value: Int get() = coins.sumOf { if (it == CoinFace.HEADS) 3 else 2 }

    /** 6 = old yin (changing yin), 7 = young yang, 8 = young yin, 9 = old yang (changing yang) */
    val isChanging: Boolean get() = value == 6 || value == 9

    fun display(): String = coins.joinToString("") { it.display() }
}
