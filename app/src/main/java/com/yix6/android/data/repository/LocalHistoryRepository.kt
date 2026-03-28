package com.yix6.android.data.repository

import com.google.gson.Gson
import com.yix6.android.data.db.ReadingDao
import com.yix6.android.data.db.ReadingEntity
import com.yix6.android.domain.contract.HistoryRecord
import com.yix6.android.domain.contract.HistoryRepository
import com.yix6.android.domain.model.HexagramResult
import com.yix6.android.domain.model.SixThrows
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LocalHistoryRepository(
    private val dao: ReadingDao,
    private val gson: Gson = Gson(),
) : HistoryRepository {

    override fun observeAll(): Flow<List<HistoryRecord>> =
        dao.observeAll().map { entities -> entities.map { it.toRecord() } }

    override suspend fun insert(record: HistoryRecord): Long =
        dao.insert(record.toEntity())

    override suspend fun delete(id: Long) {
        dao.delete(id)
    }

    override suspend fun clearAll() {
        dao.clearAll()
    }

    private fun ReadingEntity.toRecord(): HistoryRecord = HistoryRecord(
        id = id,
        timestampMs = timestampMs,
        question = question,
        sixThrows = gson.fromJson(sixThrowsJson, SixThrows::class.java),
        result = gson.fromJson(resultJson, HexagramResult::class.java),
        interpretation = interpretation,
    )

    private fun HistoryRecord.toEntity(): ReadingEntity = ReadingEntity(
        id = id,
        timestampMs = timestampMs,
        question = question,
        sixThrowsJson = gson.toJson(sixThrows),
        resultJson = gson.toJson(result),
        interpretation = interpretation,
    )
}
