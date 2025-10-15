package com.example.moviesap.data.models

data class MovieItem(
    val averageRating: Double,
    val budget: Long,
    val contentRating: String,
    val description: String,
    val endYear: Any,
    val grossWorldwide: Long,
    val id: String,
    val isAdult: Boolean,
    val metascore: Int,
    val numVotes: Int,
    val originalTitle: String,
    val primaryImage: String,
    val primaryTitle: String,
    val releaseDate: String,
    val runtimeMinutes: Int,
    val startYear: Int,
    val trailer: String,
    val type: String,
    val url: String
)