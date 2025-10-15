package com.example.moviesap.data.remote.api

import com.example.moviesap.data.remote.ImdbApiService
import com.example.moviesap.utils.Constants.Companion.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInstance {

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    }

     val api: ImdbApiService by lazy {
        retrofit.create(ImdbApiService::class.java)
    }
}