package com.yix6.android.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for persisting a hexagram reading record.
 * Complex objects (SixThrows, HexagramResult) are stored as JSON strings.
 */
@Entity(tableName = "readings")
data class ReadingEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestampMs: Long,
    val question: String,
    val sixThrowsJson: String,
    val resultJson: String,
    val interpretation: String,
)
