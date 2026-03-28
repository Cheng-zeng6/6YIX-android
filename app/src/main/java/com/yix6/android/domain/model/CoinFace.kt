package com.yix6.android.domain.model

/** Represents which face of a coin is showing. */
enum class CoinFace {
    HEADS,
    TAILS;

    fun display(): String = if (this == HEADS) "H" else "T"
}
