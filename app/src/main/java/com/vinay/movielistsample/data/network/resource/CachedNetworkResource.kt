package com.vinay.movielistsample.data.network.resource

import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.toLiveData
import com.vinay.movielistsample.AppExecutors
import io.reactivex.Maybe
import timber.log.Timber

/**
 * Provides caching for network resources. A started request will complete even if
 * the observer is removed, and stored in the cache.
 */

interface ICachedNetworkResourceCallback<LocalType, RemoteType, ResponseType> :
    INetworkResourceCallback<LocalType, RemoteType, ResponseType> {

    override fun onSuccessPostMap(it: LocalType) {
        saveToCache(it)
    }

    @WorkerThread
    fun saveToCache(item: LocalType)

    @MainThread
    fun shouldFetch(data: LocalType?): Boolean

    @MainThread
    fun loadFromCache(): LiveData<LocalType?>
}

/**
 * [NetworkRequest] which adds support for caching the response
 */
class CachedNetworkRequest<LocalType, RemoteType, ResponseType>
@MainThread constructor(
    appExecutors: AppExecutors,
    private val cb: ICachedNetworkRequestCallback<LocalType, RemoteType, ResponseType>
) : NetworkRequest<ResponseType>(appExecutors, cb) {

    override fun createNetworkRequest(): Maybe<ResponseType> {
        return super.createNetworkRequest()
            .doOnSuccess {
                cb.saveCallResult(cb.mapToLocal(cb.extractData(it)))
            }
    }
}

/**
 * Base callback for [CachedNetworkRequest]
 */
interface ICachedNetworkRequestCallback<LocalType, RemoteType, ResponseType> :
    INetworkResourceCallback<LocalType, RemoteType, ResponseType> {

    @WorkerThread
    fun saveCallResult(item: LocalType)
}
