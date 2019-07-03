package com.vinay.movielistsample.di

import android.app.Activity
import dagger.BindsInstance
import dagger.Subcomponent

/**
 * Dagger2 AppComponent configuration
 */

@ActivityScope
@Subcomponent
interface ActivityComponent {

//    fun navigator(): Navigator

    @Subcomponent.Builder
    interface Builder {

        @BindsInstance
        fun activity(activity: Activity): Builder

        fun build(): ActivityComponent
    }
}
