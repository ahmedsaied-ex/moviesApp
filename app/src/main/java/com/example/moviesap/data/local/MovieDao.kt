package com.example.moviesap.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {

    // returns a Flow which emits current cached list and updates when DB changes
    @Query("SELECT * FROM movies")
    fun getAllMovies(): Flow<List<MovieEntity>>

    // get all movies as a one-shot suspend function
    @Query("SELECT * FROM movies")
    suspend fun getAllMoviesOnce(): List<MovieEntity>

    @Query("SELECT * FROM movies")
    fun getAllMoviesPaging(): PagingSource<Int, MovieEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE) // insert or replace (upsert)
    suspend fun insertMovies(movies: List<MovieEntity>)

    // optional: clear cache
    @Query("DELETE FROM movies")
    suspend fun clearAll()
}
