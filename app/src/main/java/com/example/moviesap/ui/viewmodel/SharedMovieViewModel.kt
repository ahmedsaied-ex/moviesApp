package com.example.moviesap.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.moviesap.data.models.MovieItem
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SharedMovieViewModel @Inject constructor() : ViewModel() {

    private val _selectedMovie = MutableLiveData<MovieItem?>()
    val selectedMovie: LiveData<MovieItem?> get() = _selectedMovie

    fun setMovie(movie: MovieItem) {
        _selectedMovie.value = movie
    }
}