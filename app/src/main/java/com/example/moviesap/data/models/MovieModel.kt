package com.example.moviesap.data.models


data class MovieModel(
    val id: String,
    val title: String,
    val image: String?,
    val description: String?,
    val rating: Double?,
    val releaseDate: String?
)
