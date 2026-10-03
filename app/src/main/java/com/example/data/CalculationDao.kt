package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CalculationDao {
    @Query("SELECT * FROM calculation_history ORDER BY timestamp DESC LIMIT 100")
    fun getAllHistory(): Flow<List<CalculationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: CalculationEntity)

    @Query("DELETE FROM calculation_history")
    suspend fun clearHistory()

    @Query("DELETE FROM calculation_history WHERE id = :id")
    suspend fun deleteById(id: Long)
}
