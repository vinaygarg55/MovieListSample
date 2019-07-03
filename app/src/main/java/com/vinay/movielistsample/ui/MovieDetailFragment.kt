package com.vinay.movielistsample.ui

import android.os.Bundle
import androidx.navigation.fragment.navArgs
import com.jakewharton.rxbinding3.widget.textChanges
import com.vinay.movielistsample.R

class MovieDetailFragment : BaseFragment<com.vinay.movielistsample.databinding.FragmentMovieDetailBinding>() {

    override val layoutRes = R.layout.fragment_movie_detail
    val args: MovieDetailFragmentArgs by navArgs()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)
        val viewModel = getViewModel(MovieDetailViewModel::class.java).also {
            binding.vm = it
        }
        viewModel.setMovieId(args.movieId)
    }
}
