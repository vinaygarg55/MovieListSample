package com.vinay.movielistsample.util.ext

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.vinay.movielistsample.ui.NetworkState
import com.vinay.movielistsample.util.PagingRequestHelper

private fun PagingRequestHelper.StatusReport.getErrorMessage(type: PagingRequestHelper.RequestType?): String {
    val error =
        if (type != null) {
            getErrorFor(type)?.message
        } else {
            PagingRequestHelper.RequestType.values().mapNotNull {
                getErrorFor(it)?.message
            }.firstOrNull()
        }
    return error ?: "Unknown error"
}

fun PagingRequestHelper.createStatusLiveData(type: PagingRequestHelper.RequestType? = null): LiveData<NetworkState> {
    val liveData = MutableLiveData<NetworkState>()
    addListener { report ->
        when {
            report.hasRunning(type) -> liveData.postValue(NetworkState.loading)
            report.hasError(type) -> liveData.postValue(
                NetworkState.error(report.getErrorMessage(type))
            )
            else -> liveData.postValue(NetworkState.success)
        }
    }

    return liveData
}

fun PagingRequestHelper.StatusReport.hasRunning(type: PagingRequestHelper.RequestType? = null): Boolean {
    return when (type) {
        PagingRequestHelper.RequestType.INITIAL -> initial == PagingRequestHelper.Status.RUNNING
        PagingRequestHelper.RequestType.BEFORE -> before == PagingRequestHelper.Status.RUNNING
        PagingRequestHelper.RequestType.AFTER -> after == PagingRequestHelper.Status.RUNNING
        else -> hasRunning()
    }
}

fun PagingRequestHelper.StatusReport.hasError(type: PagingRequestHelper.RequestType? = null): Boolean {
    return when (type) {
        PagingRequestHelper.RequestType.INITIAL -> initial == PagingRequestHelper.Status.FAILED
        PagingRequestHelper.RequestType.BEFORE -> before == PagingRequestHelper.Status.FAILED
        PagingRequestHelper.RequestType.AFTER -> after == PagingRequestHelper.Status.FAILED
        else -> hasRunning()
    }
}
