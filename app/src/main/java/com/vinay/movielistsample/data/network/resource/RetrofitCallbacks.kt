package com.vinay.movielistsample.data.network.resource

import androidx.annotation.StringRes
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import com.vinay.movielistsample.R
import com.vinay.movielistsample.ui.NetworkState
import retrofit2.Response
import timber.log.Timber
import java.net.HttpURLConnection

interface IRetrofitNetworkRequestCallback<RemoteType : Any> :
    INetworkRequestCallback<Response<RemoteType>> {

    private data class JsonErrors(val errors: List<JsonError>)
    private data class JsonError(
        val httpStatus: Int,
        val code: String,
        val source: HashMap<String, Any>,
        val userMessage: String?,
        val details: List<ErrorDetail>
    )

    private data class ErrorDetail(
        val title: String,
        val message: String
    )

    override fun getErrorState(response: Response<RemoteType>): NetworkState {
        return when (response.code()) {
            HttpURLConnection.HTTP_FORBIDDEN -> NetworkState.error(response.message(), R.string.network_error_forbidden)
            HttpURLConnection.HTTP_BAD_REQUEST -> parseErrorBody(response)
            else -> NetworkState.error(response.message(), R.string.network_error_unknown)
        }
    }

    @StringRes
    private fun parseErrorBody(response: Response<RemoteType>): NetworkState {
        val gson = GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create()
        return try {
            val body: JsonErrors? = gson.fromJson(response.errorBody()?.string(), JsonErrors::class.java)
            NetworkState.error(body?.errors?.firstOrNull()?.userMessage)
        } catch (e: JsonSyntaxException) {
            Timber.w(e)
            NetworkState.error(R.string.network_error_unknown)
        }
    }

    override fun isSuccess(response: Response<RemoteType>) = response.isSuccessful
}

/**
 * Implementation of [INetworkResourceCallback] for Retrofit and [Response]
 */
interface IRetrofitNetworkResourceCallback<LocalType, RemoteType : Any> :
    INetworkResourceCallback<LocalType, RemoteType, Response<RemoteType>>,
    IRetrofitNetworkRequestCallback<RemoteType> {

    override fun extractData(response: Response<RemoteType>): RemoteType {
        return requireNotNull(response.body())
    }
}

/**
 * Interface which combines [IRetrofitNetworkResourceCallback] and [ICachedNetworkResourceCallback]
 */
interface IRetrofitCachedNetworkResourceCallback<LocalType, RemoteType : Any> :
    IRetrofitNetworkResourceCallback<LocalType, RemoteType>,
    ICachedNetworkResourceCallback<LocalType, RemoteType, Response<RemoteType>>

/**
 * Interface which combines [IRetrofitCachedNetworkRequestCallback] and [ICachedNetworkRequestCallback].
 * Suitable for POST, PUT and PATCH requests * which require the response to be stored in the cache.
 */
interface IRetrofitCachedNetworkRequestCallback<LocalType, RemoteType : Any> :
    IRetrofitNetworkResourceCallback<LocalType, RemoteType>,
    ICachedNetworkRequestCallback<LocalType, RemoteType, Response<RemoteType>>
