package com.sioptik.main.riwayat_repository

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RiwayatDao {
    @Query("SELECT * FROM riwayat ORDER BY date DESC")
    fun getAll(): Flow<List<RiwayatEntity>>
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(riwayat: RiwayatEntity)
    @Delete
    suspend fun delete(riwayat: RiwayatEntity)
    @Query("DELETE FROM riwayat")
    suspend fun deleteAll()
}