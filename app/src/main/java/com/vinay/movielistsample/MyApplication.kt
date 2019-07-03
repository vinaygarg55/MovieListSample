package com.vinay.movielistsample

import android.app.Activity
import android.app.Application
import com.vinay.movielistsample.di.AppComponent
import com.vinay.movielistsample.di.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import javax.inject.Inject

class MyApplication : Application(), HasActivityInjector{

    private lateinit var component: AppComponent

    @Inject
    lateinit var activityInjector: DispatchingAndroidInjector<Activity>


    override fun activityInjector(): AndroidInjector<Activity> {
        return activityInjector
    }


    override fun onCreate() {
        super.onCreate()
        component = DaggerAppComponent.builder().application(this).build()
        component.inject(this)
    }
}
