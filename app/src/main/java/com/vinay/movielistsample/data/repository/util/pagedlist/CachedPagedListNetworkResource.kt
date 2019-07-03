package com.vinay.movielistsample.data.repository.util.pagedlist

import androidx.paging.DataSource
import androidx.paging.PagedList
import com.vinay.movielistsample.data.network.resource.CachedEntity
import com.vinay.movielistsample.data.network.resource.PagedListNetworkResource
import com.vinay.movielistsample.data.network.resource.PagedListResourceCallback
import timber.log.Timber

/**
 * Implementation of [PagedListNetworkResource] with support for local caching
 */
open class CachedPagedListNetworkResource<LocalType : CachedEntity>(
    factory: DataSource.Factory<Int, LocalType>,
    private val cacheDuration: Int,
    pagingCallback: PagedListResourceCallback<LocalType>,
    initialLoadKey: Int? = null,
    config: PagedList.Config = DEFAULT_CONFIG
) : PagedListNetworkResource<LocalType, Int>(
    dataSourceFactory = factory.mapByPage {
        mapper(
            it,
            cacheDuration,
            pagingCallback
        )
    },
    pagingCallback = pagingCallback,
    config = config,
    initialLoadKey = initialLoadKey
) {

    companion object {

        fun <LocalType : CachedEntity> mapper(
            page: List<LocalType>,
            cacheDuration: Int,
            pagingCallback: PagedListResourceCallback<LocalType>
        ): List<LocalType> {
            val shouldRefresh = page.any {
                it.shouldRefresh(cacheDuration)
            }
            if (shouldRefresh) {
                Timber.d("REFRESHING")
                pagingCallback.refresh()
            }
            return page
        }
    }
}
