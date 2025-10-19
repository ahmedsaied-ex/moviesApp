package com.example.moviesap.ui.states

import com.example.moviesap.data.models.MovieItem

sealed class UiState {
    object Loading : UiState()
    data class Success(val movies: List<MovieItem>, val isFromCache: Boolean = false) : UiState()
    data class Error(val message: String) : UiState()
}
