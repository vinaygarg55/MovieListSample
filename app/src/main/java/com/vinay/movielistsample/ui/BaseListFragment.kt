package com.vinay.movielistsample.ui

import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.vinay.movielistsample.common.observe
import com.vinay.movielistsample.ui.recyclerview.DataBoundPagedListAdapter
import com.vinay.movielistsample.ui.util.viewmodel.PagedListViewModel
import io.reactivex.rxkotlin.plusAssign

/**
 * [BaseFragment] that adds helper methods for displaying lists.
 */
abstract class BaseListFragment<TBinding : ViewDataBinding> : BaseFragment<TBinding>() {

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
        adapter.lifecycleOwner = viewLifecycleOwner
        subscribe(adapter.retryClicks, viewModel::retry)
        clickHandler?.let { subscribe(adapter.clicks, it) }

        viewModel.isRefreshing.observe(viewLifecycleOwner) {
            if (it) isRefreshing = true
        }
        viewModel.items.observe(viewLifecycleOwner) {
            if (scrollToTopOnUpdate && recyclerView.computeVerticalScrollOffset()
                < SCROLL_TO_POSITION_LIMIT || isRefreshing
            )
                adapter.submitList(it) {
                    recyclerView.scrollToPosition(0)
                    isRefreshing = false
                }
            else {
                adapter.submitList(it)
            }
        }
        viewModel.networkState.observe(viewLifecycleOwner, adapter::updateEndLoadingState)
        viewModel.frontLoadingState.observe(viewLifecycleOwner, adapter::updateFrontLoadingState)
//        viewModel.endLoadingState.observe(viewLifecycleOwner, adapter::updateEndLoadingState)
        return adapter
    }

    /**
     * Initiates an adapter with a [RecyclerView], [PagedListViewModel] and optional click handler.
     */
    protected fun <X : DataBoundPagedListAdapter<T, *>, T> initAdapter(
        adapter: X,
        recyclerView: RecyclerView,
        resource: PagedListViewModel<T>,
        hasFixedSize: Boolean = true,
        clickHandler: ((T) -> Unit)? = null
    ): X {
        // TODO: figure out which can be set to true
        recyclerView.setHasFixedSize(hasFixedSize)
        recyclerView.adapter = adapter
        adapter.lifecycleOwner = this
        resource.items.observe(this, adapter::submitList)
        resource.networkState.observe(this, adapter::updateEndLoadingState)
        subscribe(adapter.retryClicks, resource::retry)
        clickHandler?.let { subscribe(adapter.clicks, it) }

        return adapter
    }

    companion object {
        /**
         * Maximum offset from first item which to scroll to the first item on list change
         */
        const val SCROLL_TO_POSITION_LIMIT = 100
        var isRefreshing = false
    }
}
