package com.vinay.movielistsample.util.ext

import androidx.lifecycle.MutableLiveData
import com.vinay.movielistsample.R
import com.vinay.movielistsample.data.Resource
import com.vinay.movielistsample.data.Status
import io.reactivex.Flowable

/**
 * Binds the _networkError message of the Resource<T> to an instance of MutableLiveData<String>
 */
fun <T> Resource<T>.bindNetworkError(error: MutableLiveData<Int>) {
    if (this.status == Status.ERROR) {
        val message = this.stringRes ?: R.string.network_error_unknown
        error.postValue(message)
    }
}

/**
 * Binds the _networkError message of the Resource<T> to an instance of MutableLiveData<String>
 */
fun <T> Resource<T>.bindLoadingStatus(error: MutableLiveData<Boolean>) {
    if (this.status == Status.LOADING) error.postValue(true)
    else error.postValue(false)
}

fun <T> Flowable<Resource<List<T>>>.mapToSingle(): Flowable<Resource<T>> {
    return this.map {
        Resource(it.status, it.data?.singleOrNull(), it.message, it.stringRes)
    }
}

inline fun <T, O> Flowable<Resource<T>>.mapData(crossinline f: (T?) -> O): Flowable<Resource<O>> {
    return this.map {
        Resource(it.status, f.invoke(it.data), it.message, it.stringRes)
    }
}
