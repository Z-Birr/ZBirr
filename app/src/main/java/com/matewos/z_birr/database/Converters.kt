package com.matewos.z_birr.database

import androidx.room.TypeConverter
import java.sql.Time
import java.text.SimpleDateFormat
import java.util.*

class Converters {
    @TypeConverter
    fun fromString(value: String): Calendar {
        val calendar = Calendar.getInstance()
        calendar.set(
            value.substring(0, 4).toInt(),
            value.substring(5, 7).toInt(),
            value.substring(8, 10).toInt(),
            value.substring(11, 13).toInt(),
            value.substring(14, 16).toInt(),
            value.substring(17, 19).toInt()
        )
        return calendar
    }

    @TypeConverter
    fun timeToString(date: Calendar): String {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        return simpleDateFormat.format(date.time).toString()
    }
}