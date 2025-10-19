package com.example.moviesap.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviesap.data.models.MovieItem
import com.example.moviesap.repository.MovieRepository
import com.example.moviesap.ui.states.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class MoviesViewModel @Inject constructor(
    private val repo: MovieRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState

    private var allMovies: List<MovieItem> = emptyList()
    private var currentlyDisplayedMovies: List<MovieItem> = emptyList()
    private var currentPage = 0
    private val pageSize = 20 // Load 20 movies at a time

    fun fetchMovies() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                // Try to load cached data first
                val cached = repo.getCachedMoviesOnce()
                if (cached.isNotEmpty()) {
                    Log.d("MoviesViewModel", "Loaded ${cached.size} movies from cache")
                    allMovies = cached
                    loadFirstPage()

                    refreshFromNetworkSilently()
                    return@launch
                }
                // Otherwise, get data directly from network
                val result = repo.refreshFromNetwork()
                result.onSuccess { list ->
                    Log.d("MoviesViewModel", "total lists ${list.size} ")
                    allMovies = list
                    loadFirstPage()
                }.onFailure { e ->
                    Log.e("MoviesViewModel", "Error fetching movies: ${e.message}")
                    _uiState.value = UiState.Error(getReadableErrorMessage(e))
                }

            } catch (e: Exception) {
                Log.e("MoviesViewModel", "Exception in fetchMovies: ${e.message}")
                _uiState.value = UiState.Error(getReadableErrorMessage(e))
            }
        }
    }

    private fun loadFirstPage() {
        currentPage = 0
        val firstPageMovies = allMovies.take(pageSize)
        currentlyDisplayedMovies = firstPageMovies

        Log.d("MoviesViewModel", "Loading first page: ${firstPageMovies.size}")
        _uiState.value = UiState.Success(
            movies = firstPageMovies,
            isFromCache = true,
            hasMore = allMovies.size > pageSize
        )
    }

    fun loadMoreMovies() {
        // Prevent multiple simultaneous calls
        if (_uiState.value is UiState.LoadingMore) {
            Log.d("MoviesViewModel", "Already loading more, ignoring request")
            return
        }

        val nextPage = currentPage + 1
        val startIndex = nextPage * pageSize

        // Check if we have more data to load
        if (startIndex >= allMovies.size) {
            Log.d("MoviesViewModel", "No more movies to load (startIndex: $startIndex, total: ${allMovies.size})")
            return
        }
        Log.d("MoviesViewModel", "Loading page $nextPage (startIndex: $startIndex)")

        viewModelScope.launch {
            _uiState.value = UiState.LoadingMore
            val endIndex = minOf(startIndex + pageSize, allMovies.size)
            val newMovies = allMovies.subList(startIndex, endIndex)
            currentPage = nextPage
            currentlyDisplayedMovies = currentlyDisplayedMovies + newMovies

            Log.d("MoviesViewModel", "Loaded ${newMovies.size} more movies. Total displayed: ${currentlyDisplayedMovies.size}")

            _uiState.value = UiState.Success(
                movies = currentlyDisplayedMovies,
                isFromCache = true,
                hasMore = endIndex < allMovies.size
            )
        }
    }

    private fun refreshFromNetworkSilently() {
        viewModelScope.launch {
            Log.d("MoviesViewModel", "Refreshing from network silently...")
            val result = repo.refreshFromNetwork()
            result.onSuccess { refreshed ->
                Log.d("MoviesViewModel", "Silent refresh successful: ${refreshed.size} movies")
                allMovies = refreshed

                // Keep current pagination state, just update the underlying data
                val endIndex = minOf((currentPage + 1) * pageSize, allMovies.size)
                currentlyDisplayedMovies = allMovies.take(endIndex)

                _uiState.value = UiState.Success(
                    movies = currentlyDisplayedMovies,
                    isFromCache = false,
                    hasMore = endIndex < allMovies.size
                )
            }.onFailure { e ->
                Log.e("MoviesViewModel", "Silent refresh failed: ${e.message}")

            }
        }
    }

    fun refreshMoviesFromApi() {
        viewModelScope.launch {
            Log.d("MoviesViewModel", "Manual refresh from API")
            _uiState.value = UiState.Loading

            val result = repo.refreshFromNetwork()
            result.onSuccess { list ->
                Log.d("MoviesViewModel", "Manual refresh successful: ${list.size} movies")
                allMovies = list
                loadFirstPage()
            }.onFailure { e ->
                Log.e("MoviesViewModel", "Manual refresh failed: ${e.message}")
                _uiState.value = UiState.Error(getReadableErrorMessage(e))
            }
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