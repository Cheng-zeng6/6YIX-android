package com.yix6.android.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.google.gson.Gson
import com.yix6.android.data.db.AppDatabase
import com.yix6.android.data.repository.LocalHistoryRepository
import com.yix6.android.domain.contract.HistoryRecord
import com.yix6.android.domain.contract.HistoryRepository
import com.yix6.android.domain.model.HexagramResult
import com.yix6.android.domain.model.SixThrows
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Activity-scoped ViewModel that holds shared session state
 * shared between Divination → Result → AI Interpret screens.
 */
class AppViewModel(application: Application) : AndroidViewModel(application) {

    private val _sixThrows = MutableStateFlow<SixThrows?>(null)
    val sixThrows: StateFlow<SixThrows?> = _sixThrows.asStateFlow()

    private val _hexagramResult = MutableStateFlow<HexagramResult?>(null)
    val hexagramResult: StateFlow<HexagramResult?> = _hexagramResult.asStateFlow()

    val repository: HistoryRepository = LocalHistoryRepository(
        dao = AppDatabase.getInstance(application).readingDao(),
        gson = Gson(),
    )

    fun setSixThrows(throws: SixThrows) {
        _sixThrows.value = throws
    }

    fun setHexagramResult(result: HexagramResult) {
        _hexagramResult.value = result
    }

    fun clearSession() {
        _sixThrows.value = null
        _hexagramResult.value = null
    }
}
