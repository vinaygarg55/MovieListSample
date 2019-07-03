package com.vinay.movielistsample.di

import androidx.lifecycle.ViewModel
import com.vinay.movielistsample.ui.MovieDetailViewModel
import com.vinay.movielistsample.ui.MovieListViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(MovieListViewModel::class)
    abstract fun bindMovieListViewModel(
        viewModel: MovieListViewModel
    ): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MovieDetailViewModel::class)
    abstract fun bindMovieDetailViewModel(
        viewModel: MovieDetailViewModel
    ): ViewModel
}
