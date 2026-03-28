package com.yix6.android.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ReadingDao {

    @Query("SELECT * FROM readings ORDER BY timestampMs DESC")
    fun observeAll(): Flow<List<ReadingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: ReadingEntity): Long

    @Query("DELETE FROM readings WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("DELETE FROM readings")
    suspend fun clearAll()
}
