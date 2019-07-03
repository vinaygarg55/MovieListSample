package com.vinay.movielistsample.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.vinay.movielistsample.data.repository.movies.MoviesRepository
import javax.inject.Inject

class MovieDetailViewModel @Inject constructor(
    private val moviesRepository: MoviesRepository
) : ViewModel() {

    private val _movieId = MutableLiveData<Int>()
    val movieId: LiveData<Int>
        get() = _movieId
    val movieDetails = Transformations.switchMap(_movieId) {
        moviesRepository.getMovieById(it)
    }


    val overview = Transformations.map(movieDetails) {
        it?.overview
    }

    val title = Transformations.map(movieDetails) {
        it?.title
    }

    val voteAverage = Transformations.map(movieDetails) {
        it?.voteAverage
    }

    val popularity = Transformations.map(movieDetails) {
        it?.popularity
    }

    val posterPath = Transformations.map(movieDetails) {
        it?.posterPath
    }

    val voteCount = Transformations.map(movieDetails) {
        it?.voteCount
    }

    val releaseDate = Transformations.map(movieDetails){
        it?.releaseDate
    }

    fun setMovieId(id: Int) {
        _movieId.postValue(id)
    }
}