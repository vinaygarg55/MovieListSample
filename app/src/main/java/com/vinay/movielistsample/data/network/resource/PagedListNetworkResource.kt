package com.vinay.movielistsample.data.network.resource

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.vinay.movielistsample.common.postUpdate
import com.vinay.movielistsample.data.Resource
import com.vinay.movielistsample.data.Status
import com.vinay.movielistsample.ui.NetworkState
import com.vinay.movielistsample.ui.util.IListResource
import com.vinay.movielistsample.util.PagingRequestHelper
import com.vinay.movielistsample.util.ext.createStatusLiveData
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.concurrent.Executor

abstract class PagedListResourceCallback<LocalType> : PagedList.BoundaryCallback<LocalType>() {
    abstract val networkState: LiveData<NetworkState>
    abstract val isRefreshing: LiveData<Boolean>
    abstract val networkStateInitial: LiveData<NetworkState>
    abstract val networkStateBefore: LiveData<NetworkState>
    abstract val networkStateAfter: LiveData<NetworkState>
    abstract fun refresh(showIndicator: Boolean = false)
    abstract fun retry()
}

object CacheDuration {
    const val LONG = 30
    const val MEDIUM = 10
    const val SHORT = 5
    const val NONE = 1
}

/**
 * Implementation of [IListResource] that provides local pagination through [PagedList]
 */
abstract class PagedListNetworkResource<LocalType, Key>(
    dataSourceFactory: DataSource.Factory<Key, LocalType>,
    protected val pagingCallback: PagedListResourceCallback<LocalType>,
    config: PagedList.Config = DEFAULT_CONFIG,
    initialLoadKey: Key? = null
) : IListResource<LocalType> {

    final override val data: LiveData<PagedList<LocalType>>
    final override val networkState = pagingCallback.networkState
    final override val networkStateBefore = pagingCallback.networkStateBefore
    final override val networkStateAfter = pagingCallback.networkStateAfter
    final override val isRefreshing = pagingCallback.isRefreshing

    init {
        val builder = LivePagedListBuilder(dataSourceFactory, config)
            .setBoundaryCallback(pagingCallback)
            .setInitialLoadKey(initialLoadKey)
        data = builder.build()
    }

    override fun refresh() {
        pagingCallback.refresh(true)
    }

    override fun retry(networkState: NetworkState) {
        pagingCallback.retry()
    }

    companion object {
        val DEFAULT_CONFIG: PagedList.Config = PagedList.Config.Builder()
            .setEnablePlaceholders(true)
            .setInitialLoadSizeHint(20)
            .setPageSize(20)
            .setPrefetchDistance(5)
            .build()
    }
}

/**
 * Default implementation of [PagedListResourceCallback]
 */
abstract class NetworkListResourceBoundaryCallback<LocalType, RemoteType>(networkExecutor: Executor) :
    PagedListResourceCallback<LocalType>() {

    private val helper = PagingRequestHelper(networkExecutor)
    protected var hasNext = true
    protected var hasPrevious = false
    override val networkState = helper.createStatusLiveData()
    override val networkStateInitial =
        helper.createStatusLiveData(PagingRequestHelper.RequestType.INITIAL)
    override val networkStateBefore =
        helper.createStatusLiveData(PagingRequestHelper.RequestType.BEFORE)
    override val networkStateAfter =
        helper.createStatusLiveData(PagingRequestHelper.RequestType.AFTER)
    override val isRefreshing = MutableLiveData<Boolean>()

    override fun retry() {
        helper.retryAllFailed()
    }

    override fun onZeroItemsLoaded() {
        loadInitial(false)
    }

    override fun refresh(showIndicator: Boolean) {
        loadInitial(showIndicator)
    }

    /**
     * Load resources after [itemAtEnd]
     */
    protected abstract fun loadAfter(itemAtEnd: LocalType? = null): Single<Resource<RemoteType>>?

    protected abstract fun loadBefore(itemAtFront: LocalType): Single<Resource<RemoteType>>?

    /**
     * Return false if the remote resource has no additional responseItems
     */
    protected open fun hasNext(response: RemoteType?): Boolean = true

    /**
     * Return false if the remote resource has no previous responseItems
     */
    protected open fun hasPrevious(response: RemoteType?): Boolean = false

    /**
     * Handle the remote response for initial load and after loading items at the end.
     */
    protected abstract fun handleResponse(itemAtEnd: LocalType?, response: RemoteType)

    /**
     * Handle the remote response for items at the front.
     */
    protected abstract fun handleResponseAtFront(itemAtFront: LocalType, response: RemoteType)

    private fun loadInitial(refresh: Boolean = false) {
        Timber.d("onZeroItemsLoaded")
        val resource = loadAfter() ?: return
        isRefreshing.postUpdate(refresh)
        helper.runIfNotRunning(PagingRequestHelper.RequestType.INITIAL) { cb ->
            resource
                .doOnSuccess {
                    isRefreshing.postUpdate(false)
                    if (it.status == Status.SUCCESS) {
                        hasNext = hasNext(it.data)
                        hasPrevious = hasPrevious(it.data)
                        it.data?.let { handleResponse(null, it) }
                        cb.recordSuccess()
                    } else {
                        hasNext = false
                        hasPrevious = false
                        it.throwable?.let { Timber.e(it) }
                        it.message?.let { Timber.e(it) }
                        cb.recordFailure(Throwable("Unknown network error"))
                    }
                }
                .doOnError {
                    Timber.e(it)
                    hasNext = false
                    hasPrevious = false
                    isRefreshing.postUpdate(false)
                    cb.recordFailure(Throwable("Unknown network error"))
                }
                .subscribeOn(Schedulers.io())
                .subscribe()
        }
    }

    override fun onItemAtFrontLoaded(itemAtFront: LocalType) {
        if (!hasPrevious) return
        val resource = loadBefore(itemAtFront) ?: return
        helper.runIfNotRunning(PagingRequestHelper.RequestType.BEFORE) { cb ->
            Timber.d("onItemAtFrontLoaded")
            resource.doOnSuccess {
                hasPrevious = hasPrevious(it.data)
                it.data?.let { handleResponseAtFront(itemAtFront, it) }
                cb.recordSuccess()
            }.doOnError {
                hasPrevious = false
                cb.recordFailure(it)
            }.subscribeOn(Schedulers.io())
                .subscribe({}, {})
        }
    }

    override fun onItemAtEndLoaded(itemAtEnd: LocalType) {
        if (!hasNext) return
        val resource = loadAfter(itemAtEnd) ?: return
        helper.runIfNotRunning(PagingRequestHelper.RequestType.AFTER) { cb ->
            Timber.d("onItemAtEndLoaded")
            resource.doOnSuccess {
                hasNext = hasNext(it.data)
                it.data?.let { handleResponse(itemAtEnd, it) }
                cb.recordSuccess()
            }.doOnError {
                hasNext = false
                cb.recordFailure(it)
            }.subscribeOn(Schedulers.io())
                .subscribe({}, {})
        }
    }
}
