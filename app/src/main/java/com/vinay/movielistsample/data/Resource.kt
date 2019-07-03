package com.vinay.movielistsample.data

import androidx.annotation.StringRes

enum class Status {
    SUCCESS,
    ERROR,
    LOADING
}

/**
 * A generic class that holds a value with its loading status.
 * @param <T>
</T> */
data class Resource<out T>(
    val status: Status,
    val data: T? = null,
    val message: String? = null,
    @StringRes val stringRes: Int? = null,
    val throwable: Throwable? = null
) {

    companion object {

        fun <T> success(data: T?): Resource<T> {
            return Resource(Status.SUCCESS, data)
        }

        fun <T> error(msg: String, data: T? = null): Resource<T> {
            return Resource(
                Status.ERROR,
                data = data,
                message = msg
            )
        }

        fun <T> error(
            @StringRes stringRes: Int,
            data: T? = null,
            throwable: Throwable? = null
        ): Resource<T> {
            return Resource(
                Status.ERROR,
                data = data,
                stringRes = stringRes,
                throwable = throwable
            )
        }

        fun <T> loading(data: T? = null): Resource<T> {
            return Resource(Status.LOADING, data = data)
        }
    }
}
