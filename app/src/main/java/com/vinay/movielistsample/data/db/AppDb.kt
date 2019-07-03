package com.vinay.movielistsample.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.vinay.movielistsample.data.db.movies.Movie
import com.vinay.movielistsample.data.db.movies.MovieCache

/**
 * API cache database
 */
const val SCHEMA_VERSION = 1

@TypeConverters(
    ZonedDateTimeConverter::class,
    InstantConverter::class,
    CollectionConverters::class
)

@Database(
    entities = [
        Movie::class
    ],
    version = SCHEMA_VERSION
)
@Suppress("TooManyFunctions")
abstract class AppDb : RoomDatabase() {


    abstract fun movieCache(): MovieCache
}
