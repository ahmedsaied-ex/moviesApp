package com.example.moviesap.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.moviesap.repository.MovieRepository
import com.example.moviesap.data.local.AppDatabase
import com.example.moviesap.data.remote.api.RetrofitInstance

//class MoviesViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(MoviesViewModel::class.java)) {
//            val dao = AppDatabase.getInstance(context).movieDao()
//            val api = RetrofitInstance().api
//            val repo = MovieRepository(dao, api)
//            @Suppress("UNCHECKED_CAST")
//            return MoviesViewModel(repo) as T
//        }
//        throw IllegalArgumentException("Unknown ViewModel class")
//    }
//}
