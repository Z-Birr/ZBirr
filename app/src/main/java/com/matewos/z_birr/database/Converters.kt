package com.matewos.z_birr.database

import androidx.room.TypeConverter
import java.text.SimpleDateFormat
import java.util.*

class Converters {
    @TypeConverter
    fun fromString(value: String): Calendar {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        sdf.parse(value)// all done
        return sdf.calendar
    }

    @TypeConverter
    fun timeToString(date: Calendar): String {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        return simpleDateFormat.format(date.time).toString()
    }
}