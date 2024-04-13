package com.sioptik.main.riwayat_repository
import com.sioptik.main.riwayat_repository.RiwayatEntity
import com.sioptik.main.riwayat_repository.RiwayatDao
import kotlinx.coroutines.flow.Flow

class RiwayatRepository(private val riwayatDao: RiwayatDao) {
    fun getAll(): Flow<List<RiwayatEntity>> {
        return riwayatDao.getAll()
    }
    suspend fun insert(riwayat: RiwayatEntity) {
        riwayatDao.insert(riwayat)
    }
    suspend fun delete(riwayat: RiwayatEntity) {
        riwayatDao.delete(riwayat)
    }
}