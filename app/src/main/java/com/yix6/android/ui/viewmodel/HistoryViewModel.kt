package com.yix6.android.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.yix6.android.data.db.AppDatabase
import com.yix6.android.data.repository.LocalHistoryRepository
import com.yix6.android.domain.contract.HistoryRecord
import com.yix6.android.domain.contract.HistoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HistoryState(
    val records: List<HistoryRecord> = emptyList(),
    val isLoading: Boolean = true,
)

class HistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: HistoryRepository = LocalHistoryRepository(
        dao = AppDatabase.getInstance(application).readingDao(),
        gson = Gson(),
    )

    private val _state = MutableStateFlow(HistoryState())
    val state: StateFlow<HistoryState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            repository.observeAll().collect { records ->
                _state.value = HistoryState(records = records, isLoading = false)
            }
        }
    }

    fun delete(id: Long) {
        viewModelScope.launch { repository.delete(id) }
    }

    fun clearAll() {
        viewModelScope.launch { repository.clearAll() }
    }

    fun insert(record: HistoryRecord) {
        viewModelScope.launch { repository.insert(record) }
    }
}
