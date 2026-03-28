package com.sixyix.android.engine

/**
 * 用最简单的方式表示一个卦：
 * - linesBottomToTop 长度必须是 6
 * - true = 阳爻
 * - false = 阴爻
 *
 * 说明：你们的 domain.SixThrows 已约定：第 1 次是初爻(底)，第 6 次是上爻(顶)
 */
data class HexagramBinary(
    val linesBottomToTop: List<Boolean>,
) {
    init {
        require(linesBottomToTop.size == 6) { "Hexagram must have 6 lines" }
    }
}
