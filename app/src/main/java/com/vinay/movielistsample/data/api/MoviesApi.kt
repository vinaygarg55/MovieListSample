package com.vinay.movielistsample.data.api

import com.vinay.movielistsample.data.api.models.ApiMovie
import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MoviesApi {

    companion object {
        const val MODULE_PATH = "list/"
    }

    @GET("{id}")
    fun getMovieList(
        @Path("id") listId: Long,
        @Query("page") page: Int,
        @Query("api_key") apiKey: String
    ): Single<Response<PaginatedList<ApiMovie>>>
}