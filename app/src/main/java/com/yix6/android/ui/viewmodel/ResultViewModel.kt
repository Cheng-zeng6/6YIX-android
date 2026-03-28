package com.yix6.android.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.yix6.android.data.engine.SimpleHexagramEngine
import com.yix6.android.domain.model.HexagramResult
import com.yix6.android.domain.model.SixThrows
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ResultState(
    val sixThrows: SixThrows? = null,
    val result: HexagramResult? = null,
)

class ResultViewModel : ViewModel() {

    private val engine = SimpleHexagramEngine()

    private val _state = MutableStateFlow(ResultState())
    val state: StateFlow<ResultState> = _state.asStateFlow()

    fun compute(sixThrows: SixThrows) {
        val result = engine.compute(sixThrows)
        _state.value = ResultState(sixThrows = sixThrows, result = result)
    }
}
