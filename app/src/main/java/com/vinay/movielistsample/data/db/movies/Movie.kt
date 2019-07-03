package com.vinay.movielistsample.data.db.movies

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.*
import com.vinay.movielistsample.BuildConfig
import com.vinay.movielistsample.data.api.models.ApiMovie
import com.vinay.movielistsample.data.network.resource.CachedEntity
import org.threeten.bp.ZonedDateTime

@Entity(tableName = "movie", inheritSuperIndices = false)
data class Movie(
    val voteAverage: Double,
    val voteCount: Int,
    @PrimaryKey val id: Int,
    val video: Boolean,
    val mediaType: String,
    val title: String,
    val popularity: Double,
    val posterPath: String,
    val originalLanguage: String,
    val originalTitle: String,
    val backdropPath: String,
    val adult: Boolean,
    val overview: String,
    val releaseDate: String
) : CachedEntity() {

    object Mapper {
        fun from(obj: ApiMovie): Movie {
            return obj.run {

                Movie(
                    id = id,
                    voteAverage = voteAverage,
                    voteCount = voteCount,
                    video = video,
                    mediaType = mediaType,
                    title = title,
                    popularity = popularity,
                    posterPath = getFullImageUrl(posterPath),
                    originalLanguage = originalLanguage,
                    originalTitle = originalTitle,
                    backdropPath = getFullImageUrl(backdropPath),
                    adult = adult,
                    overview = overview,
                    releaseDate = releaseDate
                )
            }
        }
    }
}

fun getFullImageUrl(url: String): String{

    return BuildConfig.IMAGE_BASE_URL.plus(url)

}


@Dao
interface MovieCache {

    @Query("SELECT * FROM movie WHERE id = :id")
    fun getById(id: Int): LiveData<Movie?>

    @Query("SELECT * FROM movie ORDER BY cacheIndex ASC")
    fun getAll(): DataSource.Factory<Int, Movie>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(items: List<Movie>)

    @Query("DELETE FROM movie")
    fun deleteAll()

    @Query("DELETE FROM movie WHERE cacheIndex >= :index")
    fun pruneCache(index: Int)

    @Query("UPDATE movie SET cacheUpdated = null")
    fun invalidate()

}
