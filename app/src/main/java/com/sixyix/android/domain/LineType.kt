package com.sixyix.android.domain

/**
 * 一爻的四种结果（按三枚铜钱法的常见映射）：
 * - 0 个正面 = 3 tails = 老阴（变爻）
 * - 1 个正面 = 少阴（不变）
 * - 2 个正面 = 少阳（不变）
 * - 3 个正面 = 老阳（变爻）
 */
enum class LineType(
    val headsCount: Int,
    val isYang: Boolean,
    val isChanging: Boolean,
) {
    OLD_YIN(headsCount = 0, isYang = false, isChanging = true),
    YOUNG_YIN(headsCount = 1, isYang = false, isChanging = false),
    YOUNG_YANG(headsCount = 2, isYang = true, isChanging = false),
    OLD_YANG(headsCount = 3, isYang = true, isChanging = true);

    companion object {
        fun from(throwResult: CoinThrow): LineType =
            fromHeadsCount(throwResult.headsCount())

        fun fromHeadsCount(headsCount: Int): LineType =
            entries.firstOrNull { it.headsCount == headsCount }
                ?: error("Invalid headsCount=$headsCount (must be 0..3)")
    }
}