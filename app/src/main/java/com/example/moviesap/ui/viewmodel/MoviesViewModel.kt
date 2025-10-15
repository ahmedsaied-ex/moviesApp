package com.example.moviesap.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviesap.repository.MovieRepository
import com.example.moviesap.data.models.MovieItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class MoviesViewModel(private val repo: MovieRepository) : ViewModel() {

    private val _movies = MutableStateFlow<List<MovieItem>>(emptyList())
    val movies: StateFlow<List<MovieItem>> = _movies

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun fetchMovies() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val cached = repo.getCachedMoviesOnce()
                if (cached.isNotEmpty()) {
                    _movies.value = cached
                    _loading.value = false
                    launch {
                        try {
                            val net = repo.refreshFromNetwork()
                            net.onSuccess { refreshed ->
                                _movies.value = refreshed
                            }.onFailure {
                                Log.e("NETWORK_ERROR", it.message ?: "Unknown network error")
                            }
                        } catch (e: Exception) {
                            Log.e("NETWORK_ERROR", e.message ?: "Unexpected error")
                        }
                    }
                    return@launch
                }
                val result = repo.refreshFromNetwork()
                result.onSuccess { list ->
                    _movies.value = list
                }.onFailure { e ->
                    _error.value = getReadableErrorMessage(e)
                    Log.e("NETWORK_ERROR", e.message ?: "Network error")
                }

            } catch (e: Exception) {
                _error.value = getReadableErrorMessage(e)
                Log.e("NETWORK_ERROR", e.message ?: "Unexpected error")
            } finally {
                _loading.value = false
            }
        }
    }

    suspend fun refreshMoviesFromApi(): Result<Unit> {
        _loading.value = true
        _error.value = null
        return try {
            val result = repo.refreshFromNetwork()
            result.onSuccess { list ->
                _movies.value = list.toList()
            }.onFailure { e ->
                _error.value = getReadableErrorMessage(e)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            _error.value = getReadableErrorMessage(e)
            Result.failure(e)
        } finally {
            _loading.value = false
        }
    }

    private fun getReadableErrorMessage(e: Throwable): String {
        return when (e) {
            is IOException -> "No internet connection. Please check your network."
            is HttpException -> "Server error (${e.code()}). Please try again later."
            is IllegalStateException -> "Unexpected data format received."
            else -> "Something went wrong. Please try again."
        }
    }
}
