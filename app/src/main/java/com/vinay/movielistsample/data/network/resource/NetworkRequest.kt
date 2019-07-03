package com.vinay.movielistsample.data.network.resource

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.vinay.movielistsample.AppExecutors
import com.vinay.movielistsample.common.postUpdate
import com.vinay.movielistsample.ui.NetworkState
import com.vinay.movielistsample.ui.util.IRequest
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

/**
 * Class that performs a single network request
 */
open class NetworkRequest<ResponseType>
@MainThread constructor(
    private val appExecutors: AppExecutors,
    private val cb: INetworkRequestCallback<ResponseType>
) : IRequest<ResponseType> {

    override fun retry(networkState: NetworkState) {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    private val _networkState: MediatorLiveData<NetworkState> =
        MediatorLiveData()
    override val networkState: LiveData<NetworkState>
        get() = _networkState

    override val request = prepareNetworkRequest()

    private fun prepareNetworkRequest(): Maybe<ResponseType> {
        return createNetworkRequest()
    }

    protected open fun createNetworkRequest(): Maybe<ResponseType> {
        // return a new maybe, and then on onSubscribe we either attach the request,
        // or return immediately depending on network limiter
        return cb.createNetworkRequest()
            .doOnError {
                _networkState.postUpdate(cb.getExceptionState(it))
                Timber.e(it)
            }
            .doOnSubscribe {
                _networkState.postUpdate(NetworkState.loading)
            }
            .subscribeOn(Schedulers.from(appExecutors.networkIO()))
            .observeOn(Schedulers.from(appExecutors.diskIO()))
            .filter(this::filterAndUpdateNetworkState)
            .doOnSuccess(cb::onSuccess)
            .onErrorComplete()
    }

    private fun filterAndUpdateNetworkState(response: ResponseType): Boolean {
        val filter = cb.isSuccess(response)
        if (filter) {
            _networkState.postUpdate(NetworkState.success)
        } else {
            _networkState.postUpdate(cb.getErrorState(response))
        }
        return filter
    }
}

/**
 * Callback for [NetworkRequest]
 */
interface INetworkRequestCallback<ResponseType> {
    /**
     * Called if [isSuccess] returns false.
     * @return A [NetworkState] representing the error.
     */
    fun getErrorState(response: ResponseType): NetworkState

    /**
     * Create and return the network request.
     */
    @MainThread
    fun createNetworkRequest(): Single<ResponseType>

    /**
     * Filter http errors.
     * @return True if successful, false otherwise.
     */
    fun isSuccess(response: ResponseType): Boolean

    /**
     * Called when the fetch operation throws an exception.
     * @return A [NetworkState] representing the exception.
     */
    fun getExceptionState(it: Throwable): NetworkState {
        return NetworkState.error(it.localizedMessage)
    }

    /**
     * Called on success
     */
    fun onSuccess(response: ResponseType) {}
}
