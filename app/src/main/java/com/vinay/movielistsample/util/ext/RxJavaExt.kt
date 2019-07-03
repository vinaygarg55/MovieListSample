package com.vinay.movielistsample.util.ext

import androidx.lifecycle.MutableLiveData
import com.vinay.movielistsample.R
import com.vinay.movielistsample.data.Resource
import com.vinay.movielistsample.data.Status
import com.vinay.movielistsample.data.api.PaginatedList
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import retrofit2.Response
import timber.log.Timber
import java.net.HttpURLConnection

/**
 * Converts a Single<Response<>> to a Flowable<Resource<>> and handles errors
 */
fun <T> Single<Response<T>>.toResource(): Single<Resource<T>> {
    return this
        .observeOn(Schedulers.io())
        .subscribeOn(Schedulers.io())
        .map {
            if (it.isSuccessful) {
                if (it.body() != null) {
                    Resource.success(it.body())
                } else {
                    Resource.error(it.message() ?: "")
                }
            } else {
                val stringRes = when (it.code()) {
                    HttpURLConnection.HTTP_FORBIDDEN -> R.string.network_error_forbidden
                    else -> null
                }
                when {
                    stringRes != null -> Resource.error(stringRes)
                    it.message() != null -> Resource.error(it.message() ?: "")
                    else -> Resource.error(R.string.network_error_unknown)
                }
            }
        }
        .onErrorReturn {
            Resource.error(R.string.network_error_unknown, throwable = it)
        }
}