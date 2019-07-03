package com.vinay.movielistsample.ui

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel

/**
 * [BaseActivity] that adds helper methods for data binding.
 */
abstract class DataBindingActivity<TBinding : ViewDataBinding> : BaseActivity() {

    /**
     * The Layout Resource ID for the fragment. This is inflated automatically.
     */
    abstract val layoutRes: Int
        @LayoutRes get

    protected lateinit var binding: TBinding
    /**
     * Creates the [ViewDataBinding] for this view.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, layoutRes)
        binding.lifecycleOwner = this
    }

    /**
     * Creates a [ViewModel] and binds it to the [ViewDataBinding] for this view.
     */
    protected inline fun <reified T : ViewModel> bindViewModel(
        noinline f: (TBinding.(T) -> Unit)? = null
    ): T {
        val viewModel = getViewModel(T::class.java)
        f?.invoke(binding, viewModel)
        binding.executePendingBindings()
        return viewModel
    }
}
