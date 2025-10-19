package com.example.moviesap.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    fun fetchMovies() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                // Try to load cached data first
                val cached = repo.getCachedMoviesOnce()
                if (cached.isNotEmpty()) {
                    _uiState.value = UiState.Success(cached, isFromCache = true)
                    // Attempt background refresh
                    refreshFromNetworkSilently()
                    return@launch
                }

                // Otherwise, get data directly from network
                val result = repo.refreshFromNetwork()
                result.onSuccess { list ->
                    _uiState.value = UiState.Success(list, isFromCache = false)
                }.onFailure { e ->
                    _uiState.value = UiState.Error(getReadableErrorMessage(e))
                }

            } catch (e: Exception) {
                _uiState.value = UiState.Error(getReadableErrorMessage(e))
            }
        }
    }

    private fun refreshFromNetworkSilently() {
        viewModelScope.launch {
            val result = repo.refreshFromNetwork()
            result.onSuccess { refreshed ->
                _uiState.value = UiState.Success(refreshed, isFromCache = false)
            }
        }
    }

    fun refreshMoviesFromApi() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val result = repo.refreshFromNetwork()
            result.onSuccess { list ->
                _uiState.value = UiState.Success(list, isFromCache = false)
            }.onFailure { e ->
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
