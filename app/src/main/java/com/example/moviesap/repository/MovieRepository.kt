package com.example.moviesap.repository

import com.example.moviesap.data.local.MovieDao
import com.example.moviesap.data.local.toEntity
import com.example.moviesap.data.local.toMovieItem
import com.example.moviesap.data.remote.ImdbApiService
import com.example.moviesap.data.models.MovieItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieRepository @Inject constructor(
    private val dao: MovieDao,
    private val api: ImdbApiService
) {

    suspend fun getCachedMoviesOnce(): List<MovieItem> =
        withContext(Dispatchers.IO) {
            dao.getAllMoviesOnce().map { it.toMovieItem() }
        }

    suspend fun refreshFromNetwork(): Result<List<MovieItem>> = withContext(Dispatchers.IO) {
        try {
            val response = api.getTop250Movies()
            if (response.isSuccessful) {
                val body = response.body() ?: emptyList()
                dao.insertMovies(body.map { it.toEntity() })
                Result.success(body)
            } else {
                Result.failure(Exception("Network error ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}