package com.vinay.movielistsample.ui.util

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import com.vinay.movielistsample.ui.NetworkState

/**
 * Interface for UI related interaction with a [List] resource
 */
interface IListResource<LocalType> : IResource<PagedList<LocalType>> {

    /**
     * The state of loading at the start of the list
     */
    val networkStateBefore: LiveData<NetworkState>

    /**
     * The state of loading at the end of the list
     */
    val networkStateAfter: LiveData<NetworkState>
}
