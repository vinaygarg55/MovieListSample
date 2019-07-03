package com.vinay.movielistsample.data.repository.movies

import androidx.lifecycle.LiveData
import com.vinay.movielistsample.AppExecutors
import com.vinay.movielistsample.data.api.MoviesApi
import com.vinay.movielistsample.data.api.PaginatedList
import com.vinay.movielistsample.data.api.models.ApiMovie
import com.vinay.movielistsample.data.db.AppDb
import com.vinay.movielistsample.data.db.movies.Movie
import com.vinay.movielistsample.data.db.movies.MovieCache
import com.vinay.movielistsample.data.network.resource.CacheDuration
import com.vinay.movielistsample.data.repository.util.pagedlist.CachedPagedListNetworkResource
import com.vinay.movielistsample.data.repository.util.pagedlist.CachedPaginatedListCallback
import com.vinay.movielistsample.di.ApiModule.Companion.API_KEY
import com.vinay.movielistsample.ui.util.IListResource
import io.reactivex.Single
import retrofit2.Response
import javax.inject.Inject

class MoviesRepository  @Inject constructor(
    private val appExecutors: AppExecutors,
    private val api: MoviesApi,
    private val cache: MovieCache,
    private val appDb: AppDb
) {

    fun getAll(): IListResource<Movie> {

        return CachedPagedListNetworkResource(
            cache.getAll(),
            CacheDuration.NONE,
            object : CachedPaginatedListCallback<Movie, ApiMovie>(
                appDb, appExecutors.networkIO()
            ) {

                override fun loadPage(page: Int): Single<Response<PaginatedList<ApiMovie>>> {
                    //TODO Make this list Id dynamic
                    return api.getMovieList(1131, page, API_KEY)
                }

                override fun mapToEntity(item: ApiMovie): Movie {
                    return Movie.Mapper.from(item)
                }

                override fun clearCache() {
                    cache.deleteAll()
                }

                override fun pruneCacheAfterIndex(index: Int) {
                    cache.pruneCache(index)
                }

                override fun insertItems(items: List<Movie>) {
                    cache.insert(items)
                }
            })
    }

    fun getMovieById(id: Int): LiveData<Movie?> {
        return cache.getById(id)
    }
}
