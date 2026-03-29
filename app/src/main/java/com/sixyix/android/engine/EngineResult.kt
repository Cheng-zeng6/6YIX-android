package com.sixyix.android.engine

import com.sixyix.android.domain.LineType

/**
 * 引擎输出的“最小可用结果”：
 * - lineTypesBottomToTop: 每一爻的类型（少阴/少阳/老阴/老阳）
 * - changingLineIndexes1to6: 变爻位置（1..6）
 * - primary: 本卦（阴阳 6 爻）
 * - changed: 变卦（如果没有变爻则为 null）
 */
data class EngineResult(
    val lineTypesBottomToTop: List<LineType>,
    val changingLineIndexes1to6: List<Int>,
    val primary: HexagramBinary,
    val changed: HexagramBinary?,
)
