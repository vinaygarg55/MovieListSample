package com.vinay.movielistsample.ui

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import com.vinay.movielistsample.R
import com.vinay.movielistsample.databinding.FragmentMovieListBinding
import kotlinx.android.synthetic.main.fragment_movie_list.*

class MovieListFragment : BaseListFragment<FragmentMovieListBinding>() {
    override val layoutRes = R.layout.fragment_movie_list
    lateinit var viewModel: MovieListViewModel

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = bindViewModel(MovieListViewModel::class.java) {
            vm = it
            listing = it.listing
        }


        initAdapter(MoviesListAdapter(), rv_movies, viewModel.listing, scrollToTopOnUpdate = true) {
            findNavController().navigate(
                MovieListFragmentDirections.actionMovieListFragmentToMovieDetailFragment(it.id)
            )

        }
    }
}
