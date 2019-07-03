package com.vinay.movielistsample.ui

import androidx.lifecycle.ViewModel
import com.vinay.movielistsample.data.repository.movies.MoviesRepository
import com.vinay.movielistsample.ui.util.viewmodel.PagedListViewModel
import javax.inject.Inject

class MovieListViewModel @Inject constructor(
    moviesRepository: MoviesRepository
) : ViewModel() {

    val listing = PagedListViewModel(moviesRepository.getAll())

}