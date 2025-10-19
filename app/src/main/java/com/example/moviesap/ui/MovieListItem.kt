package com.example.moviesap.ui

import com.example.moviesap.data.models.MovieItem

sealed class MovieListItem {
    data class BannerItem(val movies: List<MovieItem>) : MovieListItem()
    data class RegularItem(val movie: MovieItem) : MovieListItem()
}