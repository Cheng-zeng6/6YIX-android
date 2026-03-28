package com.sixyix.android.domain

data class CoinThrow(
    val c1: CoinFace,
    val c2: CoinFace,
    val c3: CoinFace,
) {
    fun headsCount(): Int =
        listOf(c1, c2, c3).count { it == CoinFace.HEADS }
}
