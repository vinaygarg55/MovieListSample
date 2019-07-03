@file:Suppress("TooManyFunctions")

package com.vinay.movielistsample.common

import androidx.lifecycle.*

/**
 * Extension wrapper for [LiveData.observe]
 */
fun <T> LiveData<T>.observe(owner: LifecycleOwner, observer: (T) -> Unit) =
    observe(owner, Observer<T> { v -> observer.invoke(v) })

/**
 * Extension wrapper for [Transformations.switchMap]
 */
fun <X, Y> LiveData<X>.switchMap(func: (X) -> LiveData<Y>?): LiveData<Y> = Transformations.switchMap(this, func)

/**
 * Extension wrapper for [Transformations.map]
 */
fun <X, Y> LiveData<X>.map(func: (X) -> Y): LiveData<Y> = Transformations.map(this, func)


fun <T> MutableLiveData<T>.postUpdate(newValue: T) {
    if (this.value != newValue)
        this.postValue(newValue)
}

fun <TSOURCE, TOUT> mediatorLiveData(
    source: LiveData<TSOURCE>,
    initial: TOUT? = null,
    onChanged: MediatorLiveData<TOUT>.(TSOURCE?) -> Unit
): MediatorLiveData<TOUT> {
    val liveData = MediatorLiveData<TOUT>()
    initial?.let { liveData.postValue(it) }
    liveData.addSource(source) { onChanged(liveData, it) }
    return liveData
}
