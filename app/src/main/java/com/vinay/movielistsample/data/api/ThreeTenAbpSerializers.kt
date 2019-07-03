package com.vinay.movielistsample.data.api

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import org.threeten.bp.Instant
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.lang.reflect.Type

class ZonedDateTimeSerializer : JsonSerializer<ZonedDateTime>, JsonDeserializer<ZonedDateTime> {

    override fun serialize(
        src: ZonedDateTime,
        typeOfSrc: Type,
        context: JsonSerializationContext
    ): JsonElement {
        return JsonPrimitive(FORMATTER.format(src))
    }

    @Throws(JsonParseException::class)
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): ZonedDateTime {
        return FORMATTER.parse(json.asString, ZonedDateTime.FROM)
    }

    companion object {
        private val FORMATTER = DateTimeFormatter.ISO_DATE
    }
}

class InstantSerializer : JsonSerializer<Instant>, JsonDeserializer<Instant> {

    override fun serialize(
        src: Instant,
        typeOfSrc: Type,
        context: JsonSerializationContext
    ): JsonElement {
        return JsonPrimitive(FORMATTER.format(src))
    }

    @Throws(JsonParseException::class)
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): Instant {
        return FORMATTER.parse(json.asString, Instant.FROM)
    }

    companion object {
        private val FORMATTER = DateTimeFormatter.ISO_INSTANT
    }
}
