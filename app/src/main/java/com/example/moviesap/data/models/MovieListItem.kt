package com.example.moviesap.data.models



sealed class MovieListItem {
    // Holds a list of movies to display in horizontal banner
    data class BannerItem(val movies: List<MovieItem>) : MovieListItem()

    // Regular vertical movie item
    data class VerticalItem(val movie: MovieItem) : MovieListItem()

    // Optional loading state
    object LoadingItem : MovieListItem()
}