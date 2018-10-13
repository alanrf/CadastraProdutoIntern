package com.alanrf.cadastraproduto.db

import android.arch.persistence.room.TypeConverter
import java.util.*

class DateConverters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {

        return when (value) {
            null -> null
            else -> Date(value)
        }
    }

    @TypeConverter
    fun toTimestamp(date: Date?): Long? {

        return when (date) {
            null -> null
            else -> date.time
        }
    }
}