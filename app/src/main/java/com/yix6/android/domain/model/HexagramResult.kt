package com.yix6.android.domain.model

/**
 * The computed result of a hexagram reading.
 *
 * @param originalHexagram  The primary hexagram (1-64) derived from the six throws.
 * @param changedHexagram   The resulting hexagram after changing lines are flipped; null if no
 *                          changing lines.
 * @param changingLines     1-based line numbers (bottom=1, top=6) that are changing.
 * @param originalName      Human-readable name of the primary hexagram.
 * @param changedName       Human-readable name of the changed hexagram, or null.
 * @param originalSymbol    Unicode symbol (☰☱…) or trigram combination for the primary hexagram.
 * @param changedSymbol     Unicode symbol for the changed hexagram, or null.
 */
data class HexagramResult(
    val originalHexagram: Int,
    val changedHexagram: Int?,
    val changingLines: List<Int>,
    val originalName: String,
    val changedName: String?,
    val originalSymbol: String,
    val changedSymbol: String?,
)
