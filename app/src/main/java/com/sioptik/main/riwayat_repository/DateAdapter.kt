package com.sioptik.main.riwayat_repository

import androidx.room.TypeConverter
import java.util.Date

class DateAdapter {
    @TypeConverter
    fun toDate(timestamp: Long): Date {
        return Date(timestamp)
    }
    @TypeConverter
    fun toTimestamp(date: Date): Long {
        return date.time
    }
}