package com.sixyix.android.domain

/**
 * 六次抛掷（从下往上记录更符合易经：第 1 次是初爻，第 6 次是上爻）
 */
data class SixThrows(
    val t1: CoinThrow,
    val t2: CoinThrow,
    val t3: CoinThrow,
    val t4: CoinThrow,
    val t5: CoinThrow,
    val t6: CoinThrow,
) {
    fun asListBottomToTop(): List<CoinThrow> = listOf(t1, t2, t3, t4, t5, t6)

    fun lineTypesBottomToTop(): List<LineType> =
        asListBottomToTop().map { LineType.from(it) }

    fun primaryIsYangBottomToTop(): List<Boolean> =
        lineTypesBottomToTop().map { it.isYang }

    fun changingLineIndexes1to6(): List<Int> =
        lineTypesBottomToTop()
            .mapIndexedNotNull { index, lineType ->
                val lineIndex1to6 = index + 1
                if (lineType.isChanging) lineIndex1to6 else null
            }
}