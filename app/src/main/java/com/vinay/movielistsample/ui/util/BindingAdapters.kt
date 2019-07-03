@file:Suppress("unused")

package com.vinay.movielistsample.ui.util

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import timber.log.Timber


class BindingAdapters


@BindingAdapter("imageSrc")
fun ImageView.imageSrc(imgURL: String?) {
    if (imgURL == null) return
    try {
        Glide.with(this)
            .load(imgURL)
            .into(this)
    } catch (e: java.lang.IllegalArgumentException) {
        Timber.w(e)
    }
}
