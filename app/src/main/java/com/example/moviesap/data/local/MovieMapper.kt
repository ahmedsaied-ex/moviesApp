package com.example.moviesap.data.local

import com.example.moviesap.data.models.MovieItem


// Convert API model to Database model
fun MovieEntity.toMovieItem(): MovieItem = MovieItem(
    averageRating = this.averageRating ?: 0.0,
    budget = this.budget ?: 0L,
    contentRating = this.contentRating ?: "",
    description = this.description ?: "",
    endYear = this.endYear ?: "",
    grossWorldwide = this.grossWorldwide ?: 0L,
    id = this.id,
    isAdult = this.isAdult ?: false,
    metascore = this.metascore ?: 0,
    numVotes = this.numVotes ?: 0,
    originalTitle = this.originalTitle ?: "",
    primaryImage = this.primaryImage ?: "",
    primaryTitle = this.primaryTitle ?: "",
    releaseDate = this.releaseDate ?: "",
    runtimeMinutes = this.runtimeMinutes ?: 0,
    startYear = this.startYear ?: 0,
    trailer = this.trailer ?: "",
    type = this.type ?: "",
    url = this.url ?: ""
)

// Convert Database model back to API model
fun MovieItem.toEntity(): MovieEntity = MovieEntity(
    id = this.id,
    averageRating = this.averageRating,
    budget = this.budget,
    contentRating = this.contentRating,
    description = this.description,
    endYear = this.endYear?.toString() ?: null,
    grossWorldwide = this.grossWorldwide,
    isAdult = this.isAdult,
    metascore = this.metascore,
    numVotes = this.numVotes,
    originalTitle = this.originalTitle,
    primaryImage = this.primaryImage,
    primaryTitle = this.primaryTitle,
    releaseDate = this.releaseDate,
    runtimeMinutes = this.runtimeMinutes,
    startYear = this.startYear,
    trailer = this.trailer,
    type = this.type,
    url = this.url
)
