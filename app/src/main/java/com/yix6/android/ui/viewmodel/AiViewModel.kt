package com.yix6.android.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yix6.android.data.ai.FakeAiInterpreter
import com.yix6.android.domain.contract.InterpretStyle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AiState(
    val question: String = "",
    val style: InterpretStyle = InterpretStyle.NORMAL,
    val interpretation: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
)

class AiViewModel : ViewModel() {

    private val interpreter = FakeAiInterpreter()

    private val _state = MutableStateFlow(AiState())
    val state: StateFlow<AiState> = _state.asStateFlow()

    fun setQuestion(q: String) {
        _state.value = _state.value.copy(question = q, error = null)
    }

    fun setStyle(style: InterpretStyle) {
        _state.value = _state.value.copy(style = style)
    }

    fun interpret(
        hexagramName: String,
        changedName: String?,
        changingLines: List<Int>,
    ) {
        val s = _state.value
        _state.value = s.copy(isLoading = true, error = null, interpretation = "")
        viewModelScope.launch {
            try {
                val result = interpreter.interpret(
                    question = s.question,
                    hexagramName = hexagramName,
                    changedName = changedName,
                    changingLines = changingLines,
                    style = s.style,
                )
                _state.value = _state.value.copy(isLoading = false, interpretation = result)
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Unknown error",
                )
            }
        }
    }
}
