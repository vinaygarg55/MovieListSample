package com.vinay.movielistsample.data.db

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DbModule {
    @Provides
    @Singleton
    fun provideAppCache(context: Context): AppDb {
        return Room.databaseBuilder(context, AppDb::class.java, "cache.db")
            .fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideMovieCache(appDb: AppDb) = appDb.movieCache()
}
