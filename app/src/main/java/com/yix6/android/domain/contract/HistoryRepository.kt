package com.yix6.android.domain.contract

import com.yix6.android.domain.model.HexagramResult
import com.yix6.android.domain.model.SixThrows
import kotlinx.coroutines.flow.Flow

/** Data model for a stored history record. */
data class HistoryRecord(
    val id: Long = 0,
    val timestampMs: Long,
    val question: String,
    val sixThrows: SixThrows,
    val result: HexagramResult,
    val interpretation: String,
)

/** Contract for persisting and retrieving past readings. */
interface HistoryRepository {
    /** Observe all history records ordered by newest first. */
    fun observeAll(): Flow<List<HistoryRecord>>

    /** Insert a new record and return its generated id. */
    suspend fun insert(record: HistoryRecord): Long

    /** Delete a record by id. */
    suspend fun delete(id: Long)

    /** Clear all history. */
    suspend fun clearAll()
}
