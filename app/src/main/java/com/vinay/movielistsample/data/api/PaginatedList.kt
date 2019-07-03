package com.vinay.movielistsample.data.api

data class PaginatedList<out T>(
    val totalResults: Int,
    val page : Int,
    val totalPages: Int,
    val results: List<T>
)
