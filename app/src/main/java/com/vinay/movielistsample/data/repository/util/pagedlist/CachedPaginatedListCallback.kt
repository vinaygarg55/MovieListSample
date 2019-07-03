package com.vinay.movielistsample.data.repository.util.pagedlist

import com.vinay.movielistsample.data.Resource
import com.vinay.movielistsample.data.api.PaginatedList
import com.vinay.movielistsample.data.db.AppDb
import com.vinay.movielistsample.data.network.resource.CachedEntity
import com.vinay.movielistsample.data.network.resource.NetworkListResourceBoundaryCallback
import com.vinay.movielistsample.util.ext.toResource
import io.reactivex.Single
import org.threeten.bp.Instant
import retrofit2.Response
import java.util.concurrent.Executor

abstract class CachedPaginatedListCallback<LocalType : CachedEntity, RemoteType>(
    private val appDb: AppDb,
    networkExecutor: Executor,
    private val apiPageSize: Int = API_PAGE_SIZE
) : NetworkListResourceBoundaryCallback<LocalType, PaginatedList<RemoteType>>(networkExecutor) {

    /**
     * The page size of the API.
     */
    companion object {
        const val API_PAGE_SIZE = 20
    }

    private fun getPage(index: Int?): Int {
        return if (index == null) 1 else (index + 1) / apiPageSize + 1
    }

    private fun getFirstIndexForPage(page: Int): Int {
        return (page - 1) * apiPageSize
    }

    override fun loadAfter(itemAtEnd: LocalType?): Single<Resource<PaginatedList<RemoteType>>>? {
        return loadPage(getPage(itemAtEnd?.cacheIndex)).toResource()
    }

    /**
     * Load the specified page from the API.
     */
    abstract fun loadPage(page: Int): Single<Response<PaginatedList<RemoteType>>>

    override fun hasNext(response: PaginatedList<RemoteType>?): Boolean {

        return response.let {
            if (it == null) {
                false
            } else
            it.page < it.totalPages
        }
    }

    override fun loadBefore(itemAtFront: LocalType): Single<Resource<PaginatedList<RemoteType>>>? {
        TODO("not implemented")
    }

    override fun handleResponseAtFront(
        itemAtFront: LocalType,
        response: PaginatedList<RemoteType>
    ) {
        TODO("not implemented")
    }

    override fun hasPrevious(response: PaginatedList<RemoteType>?) = false

    /**
     * Map the RemoteType to the LocalType.
     */
    abstract fun mapToEntity(item: RemoteType): LocalType

    /**
     * Clear the cache for this resource.
     */
    abstract fun clearCache()

    /**
     * Clear cache responseItems after given index.
     */
    abstract fun pruneCacheAfterIndex(index: Int)

    /**
     * Insert responseItems into the cache.
     */
    abstract fun insertItems(items: List<LocalType>)

    override fun handleResponse(itemAtEnd: LocalType?, response: PaginatedList<RemoteType>) {
        val page = getPage(itemAtEnd?.cacheIndex)
        val startIndex = getFirstIndexForPage(page)
        val now = Instant.now()
        val items = response.results.mapIndexed { pageIndex, apiPost ->
            mapToEntity(apiPost).apply {
                cacheIndex = pageIndex + startIndex
                cacheUpdated = now
            }
        }
        appDb.runInTransaction {

            if (itemAtEnd == null) clearCache()
            else if (!hasNext(response)) pruneCacheAfterIndex(response.totalResults)
            insertItems(items)
        }
    }
}
