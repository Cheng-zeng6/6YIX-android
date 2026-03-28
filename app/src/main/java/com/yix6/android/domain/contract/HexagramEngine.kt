package com.yix6.android.domain.contract

import com.yix6.android.domain.model.HexagramResult
import com.yix6.android.domain.model.SixThrows

/** Contract for computing hexagram results from six throws. */
interface HexagramEngine {
    /**
     * Compute the [HexagramResult] from the provided [SixThrows].
     */
    fun compute(sixThrows: SixThrows): HexagramResult
}
