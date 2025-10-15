package com.example.moviesap.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviesap.data.models.MovieItem
import com.example.moviesap.data.models.MovieModel
import com.example.moviesap.data.remote.api.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class MoviesViewModel : ViewModel() {

    private val _movies =MutableStateFlow<List<MovieItem>>(emptyList())
    val movies: MutableStateFlow<List<MovieItem>> = _movies

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun fetchMovies() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val response = RetrofitInstance().api.getTop250Movies()
                if (response.isSuccessful && response.body() != null) {
                    _movies.value = response.body()!!
                } else {
                    _error.value = "Error: ${response.code()} - ${response.message()}"
                }
            } catch (e: IOException) {
                // No internet or network failure
                _error.value = "Network error. Please check your connection."
            } catch (e: HttpException) {
                // HTTP protocol error
                _error.value = "Server error: ${e.message()}"
            } catch (e: Exception) {
                // Unexpected error
                _error.value = "Unexpected error: ${e.localizedMessage}"
            } finally {
                _loading.value = false
            }
        }
    }


}