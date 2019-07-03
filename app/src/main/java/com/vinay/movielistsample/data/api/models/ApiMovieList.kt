package com.vinay.movielistsample.data.api.models

import org.threeten.bp.ZonedDateTime

data class ApiMovie (

    val voteAverage: Double,
    val voteCount: Int,
    val id: Int,
    val video: Boolean,
    val mediaType: String,
    val title: String,
    val popularity: Double,
    val posterPath: String,
    val originalLanguage: String,
    val originalTitle: String,
    val backdropPath: String,
    val adult: Boolean,
    val overview: String,
    val releaseDate: String
)