package com.example.moviesap.di

import android.content.Context
import androidx.room.Room
import com.example.moviesap.data.local.AppDatabase
import com.example.moviesap.data.local.MovieDao
import com.example.moviesap.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            Constants.MOVIE_DATABASE
        ).build()
    }

    @Provides
    @Singleton
    fun provideMovieDao(database: AppDatabase): MovieDao {
        return database.movieDao()
    }
}