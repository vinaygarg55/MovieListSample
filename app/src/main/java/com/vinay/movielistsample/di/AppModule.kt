package com.vinay.movielistsample.di

import android.app.Application
import android.content.Context
import com.vinay.movielistsample.MyApplication
import com.vinay.movielistsample.data.db.DbModule
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(
    includes = [
        UiModule::class,
        DbModule::class
    ],
    subcomponents = [ActivityComponent::class]
)
class AppModule {

    @Provides
    @Singleton
    fun provideApplication(application: MyApplication): Application {
        return application
    }

    @Provides
    @Singleton
    fun provideContext(application: MyApplication): Context {
        return application.applicationContext
    }
}
