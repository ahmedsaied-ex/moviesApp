package com.example.moviesap.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movies")
data class MovieEntity(
    @PrimaryKey val id: String,
    val averageRating: Double?,
    val budget: Long?,
    val contentRating: String?,
    val description: String?,
    val endYear: String?,
    val grossWorldwide: Long?,
    val isAdult: Boolean?,
    val metascore: Int?,
    val numVotes: Int?,
    val originalTitle: String?,
    val primaryImage: String?,
    val primaryTitle: String?,
    val releaseDate: String?,
    val runtimeMinutes: Int?,
    val startYear: Int?,
    val trailer: String?,
    val type: String?,
    val url: String?
)
