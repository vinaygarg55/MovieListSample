package com.vinay.movielistsample.di

import androidx.lifecycle.ViewModelProvider
import com.vinay.movielistsample.MainActivity
import com.vinay.movielistsample.di.activity.MainActivityModule
import com.vinay.movielistsample.ui.MovieDetailFragment
import com.vinay.movielistsample.ui.MovieListFragment
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module(includes = [ViewModelModule::class])
abstract class UiModule {

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @ContributesAndroidInjector(modules = [MainActivityModule::class])
    abstract fun contributeMainActivity(): MainActivity


//    @ContributesAndroidInjector
//    abstract fun contributeMovieListActivity(): MovieListFragment
//
//
//    @ContributesAndroidInjector
//    abstract fun contributeMovieDetailFragment(): MovieDetailFragment

}
