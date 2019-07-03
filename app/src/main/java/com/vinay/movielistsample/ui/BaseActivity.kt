package com.vinay.movielistsample.ui

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.vinay.movielistsample.R
import com.vinay.movielistsample.common.observe
import com.vinay.movielistsample.di.ActivityComponent
import com.vinay.movielistsample.di.ViewModelFactory
import com.vinay.movielistsample.ui.recyclerview.DataBoundPagedListAdapter
import com.vinay.movielistsample.ui.util.viewmodel.PagedListViewModel
import dagger.android.support.DaggerAppCompatActivity
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import org.jetbrains.anko.contentView
import org.jetbrains.anko.find
import javax.inject.Inject
import javax.inject.Provider

/**
 * Base class with convenience methods for Movie activities.
 */
@Suppress("TooManyFunctions")
abstract class BaseActivity : DaggerAppCompatActivity() {

    protected fun requireListId() = requireInt(EXTRA_MOVIE_ID)

    private fun requireInt(tag: String): Int {
        var result = intent?.extras?.getInt(tag, -1) ?: -1
        if (result == -1) {
            result = intent?.extras?.getString(tag)?.toIntOrNull()
                ?: throw IllegalArgumentException("Missing required extra: $tag")
        }
        return result
    }

    override fun setTitle(title: CharSequence?) {
        super.setTitle(title)
        supportActionBar?.title = title
    }

    /**
     * Container for RxJava subscriptions.
     */
    protected val subscriptions = CompositeDisposable()

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var activityComponentProvider: Provider<ActivityComponent.Builder>

    lateinit var activityComponent: ActivityComponent

    /**
     * Get the ViewModel using the Dagger2 [ViewModelFactory].
     */
    protected fun <T : ViewModel?> getViewModel(java: Class<T>): T {
        return ViewModelProviders.of(this, viewModelFactory).get(java)
    }

    /**
     * Subscribes to a [Observable] and handles disposing.
     */
    protected fun <T> subscribe(stream: Observable<T>?, handler: (T) -> Unit) {
        stream?.let { subscriptions += it.subscribe(handler) {} }
    }

    /**
     * Apply custom group color
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent = activityComponentProvider.get()
            .activity(this)
            .build()
    }

    /**
     * Disposes all active RxJava subscriptions.
     */
    override fun onDestroy() {
        super.onDestroy()
        subscriptions.dispose()
    }

    /**
     * Binds to a network error field.
     */
    protected fun bindNetworkError(
        networkError: LiveData<Int>,
        view: View = find(android.R.id.content),
        action: ((view: View) -> Unit)? = null
    ) {
        networkError.observe(this) {
            it.let {
                val snackBar = Snackbar.make(view, it, Snackbar.LENGTH_LONG)
                action?.let { snackBar.setAction(R.string.retry, it) }
                val snackTextView = snackBar.view.findViewById(R.id.snackbar_text) as? TextView
                snackTextView?.setTextColor(
                    ContextCompat.getColor(
                        applicationContext,
                        android.R.color.white
                    )
                )
                snackBar.show()
            }
        }
    }

    fun showToast(message: String?) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }


    /**
     * Initiates an adapter with a [RecyclerView], [PagedListViewModel] and optional click handler.
     * Will scroll to the top of the list when a new item is inserted if the list is currently
     * already scrolled to the top
     */
    @Suppress("LongParameterList")
    protected fun <X : DataBoundPagedListAdapter<T, *>, T> initAdapter(
        adapter: X,
        recyclerView: RecyclerView,
        viewModel: PagedListViewModel<T>,
        scrollToTopOnUpdate: Boolean = false,
        hasFixedSize: Boolean = true,
        clickHandler: ((T) -> Unit)? = null
    ): X {
        recyclerView.setHasFixedSize(hasFixedSize)
        recyclerView.adapter = adapter
        adapter.lifecycleOwner = this
        subscriptions += adapter.retryClicks.subscribe(viewModel::retry)
        clickHandler?.let { subscriptions += adapter.clicks.subscribe(it) }
        viewModel.items.observe(this) {
            if (scrollToTopOnUpdate && recyclerView.computeVerticalScrollOffset() < BaseListFragment.SCROLL_TO_POSITION_LIMIT)
                adapter.submitList(it) { recyclerView.scrollToPosition(0) }
            else
                adapter.submitList(it)
        }
        viewModel.networkState.observe(this, adapter::updateEndLoadingState)
        viewModel.frontLoadingState.observe(this, adapter::updateFrontLoadingState)
//        viewModel.endLoadingState.observe(this, adapter::updateEndLoadingState)
        return adapter
    }

    companion object{
        const val EXTRA_MOVIE_ID = "extraMovieId"
    }
}
