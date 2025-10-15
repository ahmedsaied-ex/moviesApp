package com.example.moviesap.data.remote

import com.example.moviesap.data.models.MovieItem

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers

interface ImdbApiService {
    @Headers(
        "x-rapidapi-key: f57fe2e955msh09f2155912c51e3p149111jsn6113c7eebd37",
        "x-rapidapi-host: imdb236.p.rapidapi.com"
    )
    @GET("api/imdb/top250-movies")
    suspend fun getTop250Movies(): Response<List<MovieItem>>
}