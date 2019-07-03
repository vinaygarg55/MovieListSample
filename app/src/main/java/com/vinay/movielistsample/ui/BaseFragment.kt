package com.vinay.movielistsample.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import com.vinay.movielistsample.di.ViewModelFactory
import dagger.android.support.DaggerFragment
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import javax.inject.Inject

/**
 * Base class for Movie fragments.
 */
@Suppress("StringLiteralDuplication")
abstract class BaseFragment<TBinding : ViewDataBinding> : DaggerFragment() {

    protected fun requireInt(tag: String): Int {
        val result = arguments?.getInt(tag, -1)
        if (result == null || result == -1) throw IllegalArgumentException("Missing required extra: $tag")
        return result
    }

    private fun requireLong(tag: String): Long {
        val result = arguments?.getLong(tag, -1L)
        if (result == null || result == -1L) throw IllegalArgumentException("Missing required extra: $tag")
        return result
    }

    private fun requireBoolean(tag: String): Boolean {
        return arguments?.getBoolean(tag, false)
            ?: throw IllegalArgumentException("Missing required extra: $tag")
    }

    protected fun requireString(tag: String): String {
        val result = arguments?.getString(tag, null)
        if (result == null || result.isEmpty()) throw IllegalArgumentException("Missing required extra: $tag")
        return result
    }

    /**
     * The layout lesource ID for the fragment. This is inflated automatically.
     */
    abstract val layoutRes: Int
        @LayoutRes get

    /**
     * The title resource ID for the fragment. This is set automatically.
     */
    open val titleRes: Int? = null
        @StringRes get

    protected lateinit var binding: TBinding

    /**
     * Container for RxJava subscriptions.
     */
    protected val subscriptions = CompositeDisposable()

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

//    protected lateinit var navigator: Navigator

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as? BaseActivity)?.let {
//            navigator = it.activityComponent.navigator()
        } ?: throw IllegalStateException("Host activity must inherit from BaseActivity")
    }

    override fun onResume() {
        super.onResume()
        titleRes?.let { activity?.setTitle(it) }
    }

    /**
     * Get the ViewModel using the Dagger2 [ViewModelFactory].
     */
    protected open fun <T : ViewModel?> getViewModel(
        java: Class<T>,
        activityScope: Boolean = true
    ): T {
        return activity.let {
            if (activityScope && it != null) {
                ViewModelProviders.of(it, viewModelFactory).get(java)
            } else {
                ViewModelProviders.of(this, viewModelFactory).get(java)
            }
        }
    }

    /**
     * Subscribes to a [Observable] and handles disposing.
     */
    fun <T> subscribe(stream: Observable<T>, handler: (T) -> Unit) {
        subscriptions += stream.subscribe(handler) {}
    }

    /**
     * Creates the [ViewDataBinding] for this view.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, layoutRes, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }


//    /**
//     * Creates a [ViewModel] and binds it to the [ViewDataBinding] for this view.
//     */
//    // TODO: refactor with reified type
    protected inline fun <T : ViewModel> bindViewModel(
        java: Class<T>,
        f: TBinding.(T) -> Unit
    ): T {
        val viewModel = getViewModel(java)
        f.invoke(binding, viewModel)
        binding.executePendingBindings()
        return viewModel
    }

//    /**
//     * Creates a [ViewModel] and binds it to the [ViewDataBinding] for this view.
//     */
//    protected inline fun <reified T : ViewModel> bindViewModel(
//        noinline f: (TBinding.(T) -> Unit)? = null
//    ): T {
//        val viewModel = getViewModel(T::class.java)
//        f?.invoke(binding, viewModel)
//        binding.executePendingBindings()
//        return viewModel
//    }
}
