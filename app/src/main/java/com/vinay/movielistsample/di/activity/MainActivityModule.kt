package com.vinay.movielistsample.di.activity

import com.vinay.movielistsample.ui.MovieDetailFragment
import com.vinay.movielistsample.ui.MovieListFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MainActivityModule {

    @ContributesAndroidInjector
    abstract fun contributeMovieListFragment(): MovieListFragment

    @ContributesAndroidInjector
    abstract fun contributeMovieDetailFragment(): MovieDetailFragment
}