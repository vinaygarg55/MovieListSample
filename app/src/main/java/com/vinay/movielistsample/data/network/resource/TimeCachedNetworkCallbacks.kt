package com.vinay.movielistsample.data.network.resource

import androidx.room.Entity
import androidx.room.Index
import org.threeten.bp.Instant
import org.threeten.bp.temporal.ChronoUnit

interface ICachedEntity {
    var cacheIndex: Int
    var cacheUpdated: Instant?

    fun shouldRefresh(cachedMinutes: Int): Boolean {
        return cacheUpdated?.plus(
            cachedMinutes.toLong(),
            ChronoUnit.MINUTES
        )?.isBefore(Instant.now()) ?: true
    }
}

@Entity(indices = [Index(value = ["cacheIndex"], unique = true)], inheritSuperIndices = false)
abstract class CachedEntity : ICachedEntity {
    override var cacheIndex: Int = -1
    override var cacheUpdated: Instant? = null
}
