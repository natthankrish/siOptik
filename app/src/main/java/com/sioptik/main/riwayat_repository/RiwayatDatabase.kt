package com.sioptik.main.riwayat_repository

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.CoroutineScope

@Database(entities = [RiwayatEntity::class], version = 1, exportSchema = false)
abstract class RiwayatDatabase: RoomDatabase() {
    abstract fun riwayatDao(): RiwayatDao
    companion object {
        private var INSTANCE: RiwayatDatabase? = null
        fun getInstance(context: Context): RiwayatDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(context, RiwayatDatabase::class.java, "riwayat").build()
                INSTANCE = instance
                instance
            }
        }
    }
}