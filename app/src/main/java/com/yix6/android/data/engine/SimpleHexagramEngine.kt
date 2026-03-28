package com.yix6.android.data.engine

import com.yix6.android.domain.contract.HexagramEngine
import com.yix6.android.domain.model.CoinFace
import com.yix6.android.domain.model.HexagramResult
import com.yix6.android.domain.model.SixThrows
import com.yix6.android.domain.model.Throw

/**
 * Simple stub implementation of [HexagramEngine].
 *
 * The I Ching has 64 hexagrams. Each line is determined by the coin throw value:
 *  - 6 (old yin)  → solid changing line  → becomes yang in changed hexagram
 *  - 7 (young yang) → solid yang line    → stays yang
 *  - 8 (young yin)  → broken yin line    → stays yin
 *  - 9 (old yang) → broken changing line → becomes yin in changed hexagram
 *
 * A hexagram number is derived from a 6-bit binary pattern (yang=1, yin=0),
 * read from line 1 (bottom) to line 6 (top), then looked up in the King Wen sequence.
 */
class SimpleHexagramEngine : HexagramEngine {

    override fun compute(sixThrows: SixThrows): HexagramResult {
        val throws = sixThrows.throws

        // Determine yang/yin for original and changed hexagram for each line
        // yang=true, yin=false
        val originalLines = throws.map { isYang(it, original = true) }
        val changedLines = throws.map { isYang(it, original = false) }

        val hasChangingLines = throws.any { it.isChanging }

        val originalNumber = hexagramNumber(originalLines)
        val changedNumber = if (hasChangingLines) hexagramNumber(changedLines) else null

        val changingLineNumbers = sixThrows.changingLineIndices.map { it + 1 } // 1-based

        return HexagramResult(
            originalHexagram = originalNumber,
            changedHexagram = changedNumber,
            changingLines = changingLineNumbers,
            originalName = hexagramName(originalNumber),
            changedName = changedNumber?.let { hexagramName(it) },
            originalSymbol = hexagramSymbol(originalLines),
            changedSymbol = if (hasChangingLines) hexagramSymbol(changedLines) else null,
        )
    }

    /** True if the line is yang (solid) in the original hexagram. */
    private fun isYang(t: Throw, original: Boolean): Boolean = when (t.value) {
        7 -> true   // young yang → yang
        8 -> false  // young yin  → yin
        9 -> if (original) true else false  // old yang → yang originally, yin after change
        6 -> if (original) false else true  // old yin  → yin originally, yang after change
        else -> false
    }

    /**
     * Convert a list of 6 booleans (yang=true, yin=false, index 0=bottom line) to a
     * King Wen hexagram number 1-64 using the standard lookup table.
     */
    private fun hexagramNumber(lines: List<Boolean>): Int {
        // Build a 6-bit index: bit 0 = bottom line, bit 5 = top line
        // Each trigram is 3 bits; lower trigram = lines[0..2], upper = lines[3..5]
        val lowerIndex = trigramIndex(lines[0], lines[1], lines[2])
        val upperIndex = trigramIndex(lines[3], lines[4], lines[5])
        // King Wen lookup table: rows=lower trigram (0-7), cols=upper trigram (0-7)
        return KING_WEN_TABLE[lowerIndex][upperIndex]
    }

    /** Convert 3 yang/yin booleans to a trigram index (0-7) — yang=1, yin=0. */
    private fun trigramIndex(bottom: Boolean, middle: Boolean, top: Boolean): Int =
        (if (bottom) 1 else 0) or
                ((if (middle) 1 else 0) shl 1) or
                ((if (top) 1 else 0) shl 2)

    /** Build a text symbol representing the hexagram using solid/broken lines. */
    private fun hexagramSymbol(lines: List<Boolean>): String {
        // Display top line first (lines[5]) down to bottom (lines[0])
        return (5 downTo 0).joinToString("\n") { i ->
            if (lines[i]) "⚊" else "⚋"  // solid yang / broken yin
        }
    }

    private fun hexagramName(number: Int): String =
        HEXAGRAM_NAMES.getOrNull(number - 1) ?: "Hexagram $number"

    companion object {
        /**
         * King Wen sequence lookup table.
         * Index: [lower trigram index][upper trigram index] → hexagram number (1-64).
         * Trigram order: 0=☷(Earth/Kun), 1=☳(Thunder/Zhen), 2=☵(Water/Kan),
         *                3=☶(Mountain/Gen), 4=☰(Heaven/Qian), 5=☴(Wind/Xun),
         *                6=☲(Fire/Li), 7=☱(Lake/Dui)
         */
        private val KING_WEN_TABLE = arrayOf(
            //  Kun  Zhen  Kan   Gen  Qian  Xun   Li   Dui   ← upper
            intArrayOf(2, 24, 7, 15, 12, 20, 35, 45),  // Kun   lower
            intArrayOf(16, 51, 3, 27, 25, 42, 21, 17),  // Zhen
            intArrayOf(8, 40, 29, 4, 6, 59, 64, 47),   // Kan
            intArrayOf(23, 27, 4, 52, 33, 53, 56, 62),  // Gen
            intArrayOf(11, 34, 5, 26, 1, 9, 14, 43),   // Qian
            intArrayOf(46, 32, 48, 18, 44, 57, 50, 28), // Xun
            intArrayOf(36, 55, 63, 22, 13, 37, 30, 49), // Li
            intArrayOf(19, 54, 60, 41, 10, 61, 38, 58), // Dui
        )

        /** Names of the 64 hexagrams in King Wen order (index 0 = hexagram 1). */
        val HEXAGRAM_NAMES = listOf(
            "Qian (Creative)", "Kun (Receptive)", "Zhun (Difficulty at the Beginning)",
            "Meng (Youthful Folly)", "Xu (Waiting)", "Song (Conflict)",
            "Shi (The Army)", "Bi (Holding Together)", "Xiao Xu (Small Taming)",
            "Lü (Treading)", "Tai (Peace)", "Pi (Standstill)",
            "Tong Ren (Fellowship)", "Da You (Great Possession)", "Qian (Modesty)",
            "Yu (Enthusiasm)", "Sui (Following)", "Gu (Work on the Decayed)",
            "Lin (Approach)", "Guan (Contemplation)", "Shi He (Biting Through)",
            "Bi (Grace)", "Bo (Splitting Apart)", "Fu (Return)",
            "Wu Wang (Innocence)", "Da Xu (Great Taming)", "Yi (Nourishment)",
            "Da Guo (Great Preponderance)", "Kan (The Abysmal Water)", "Li (The Clinging Fire)",
            "Xian (Influence)", "Heng (Duration)", "Dun (Retreat)",
            "Da Zhuang (Great Power)", "Jin (Progress)", "Ming Yi (Darkening of the Light)",
            "Jia Ren (The Family)", "Kui (Opposition)", "Jian (Obstruction)",
            "Jie (Deliverance)", "Sun (Decrease)", "Yi (Increase)",
            "Guai (Breakthrough)", "Gou (Coming to Meet)", "Cui (Gathering Together)",
            "Sheng (Pushing Upward)", "Kun (Oppression)", "Jing (The Well)",
            "Ge (Revolution)", "Ding (The Cauldron)", "Zhen (The Arousing Thunder)",
            "Gen (Keeping Still Mountain)", "Jian (Development)", "Gui Mei (The Marrying Maiden)",
            "Feng (Abundance)", "Lü (The Wanderer)", "Xun (The Gentle Wind)",
            "Dui (The Joyous Lake)", "Huan (Dispersion)", "Jie (Limitation)",
            "Zhong Fu (Inner Truth)", "Xiao Guo (Small Preponderance)", "Ji Ji (After Completion)",
            "Wei Ji (Before Completion)",
        )
    }
}
