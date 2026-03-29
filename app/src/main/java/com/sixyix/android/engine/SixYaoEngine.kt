package com.sixyix.android.engine

import com.sixyix.android.domain.SixThrows

/**
 * 六爻引擎入口（先把“接口”定死，算法细节 Person2 再实现）
 */
interface SixYaoEngine {
    fun calculate(sixThrows: SixThrows): EngineResult
}