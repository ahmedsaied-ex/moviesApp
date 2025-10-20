package com.example.moviesap.repository

import androidx.paging.PagingData
import com.example.moviesap.data.local.MovieDao
import com.example.moviesap.data.local.toEntity
import com.example.moviesap.data.local.toMovieItem
import com.example.moviesap.data.remote.ImdbApiService
import com.example.moviesap.data.models.MovieItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.map
import kotlinx.coroutines.flow.map
@Singleton
class MovieRepository @Inject constructor(
    private val dao: MovieDao,
    private val api: ImdbApiService
) {


    fun getMoviesPager(): Flow<PagingData<MovieItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { dao.getAllMoviesPaging() }
        ).flow.map { pagingData ->
            pagingData.map { it.toMovieItem() }
        }
    }
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