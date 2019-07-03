package com.vinay.movielistsample.data.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import java.util.*

class InstantConverter {
    @TypeConverter
    fun fromTimestamp(value: Long?): Instant? {
        return if (value == null) null else Instant.ofEpochMilli(value)
    }

    @TypeConverter
    fun dateToTimestamp(date: Instant?): Long? {
        return date?.toEpochMilli()
    }
}

class ZonedDateTimeConverter {
    @TypeConverter
    fun fromTimestamp(value: Long?): ZonedDateTime? {
        return if (value == null) null else ZonedDateTime.ofInstant(
            Instant.ofEpochMilli(value),
            ZoneId.systemDefault()
        )
    }

    @TypeConverter
    fun dateToTimestamp(date: ZonedDateTime?): Long? {
        return date?.toInstant()?.toEpochMilli()
    }
}

class CollectionConverters {

    var gson = Gson()

    @TypeConverter
    fun stringListToString(someObjects: List<String>?): String? {
        return gson.toJson(someObjects)
    }

    @TypeConverter
    fun stringToStringList(data: String?): List<String>? {
        if (data == null) return Collections.emptyList()
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun stringMaptoString(someObjects: Map<String, Long>?): String? {
        return gson.toJson(someObjects)
    }

    @TypeConverter
    fun stringToStringMap(data: String?): Map<String, Long>? {
        if (data == null) return Collections.emptyMap()
        val listType = object : TypeToken<HashMap<String, Long>>() {}.type
        return gson.fromJson<HashMap<String, Long>>(data, listType)
    }
}
