package com.vinay.movielistsample.ui

import androidx.recyclerview.widget.DiffUtil
import com.vinay.movielistsample.R
import com.vinay.movielistsample.data.db.movies.Movie
import com.vinay.movielistsample.databinding.ListMoviesBinding
import com.vinay.movielistsample.ui.recyclerview.DataBoundPagedListAdapter

class MoviesListAdapter : DataBoundPagedListAdapter<Movie, ListMoviesBinding>(differ = Diff) {

    override val defaultLayoutRes = R.layout.list_movies

    override fun map(binding: ListMoviesBinding): Movie? {
        return binding.item
    }

    override fun bind(
        binding: ListMoviesBinding,
        item: Movie?,
        position: Int
    ) {
        binding.item = item
    }

    object Diff : DiffUtil.ItemCallback<Movie>() {
        override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem.title == newItem.title &&
                    oldItem.id == newItem.id &&
                    oldItem.adult == newItem.adult &&
                    oldItem.video == newItem.video &&
                    oldItem.backdropPath == newItem.backdropPath &&
                    oldItem.mediaType == newItem.mediaType &&
                    oldItem.originalLanguage == newItem.originalLanguage &&
                    oldItem.originalTitle == newItem.originalTitle &&
                    oldItem.overview == newItem.overview &&
                    oldItem.popularity == newItem.popularity &&
                    oldItem.posterPath == newItem.posterPath &&
                    oldItem.voteAverage == newItem.voteAverage &&
                    oldItem.voteCount == newItem.voteCount
        }
    }
}