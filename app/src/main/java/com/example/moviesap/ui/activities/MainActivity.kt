package com.example.moviesap.ui.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moviesap.databinding.ActivityMainBinding
import com.example.moviesap.ui.adapter.BannerMoviesAdapter
import com.example.moviesap.ui.adapter.VerticalMoviesAdapter
import com.example.moviesap.ui.fragments.MovieDetailsBottomSheet
import com.example.moviesap.ui.viewmodel.MoviesViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MoviesViewModel by viewModels()
    private val bannerAdapter by lazy {
        BannerMoviesAdapter(
            onItemClick = {movieItem ->
                val sheet = MovieDetailsBottomSheet(movieItem)
                sheet.show(supportFragmentManager, "MovieDetailsBottomSheet")
            }
        )
    }
    private val mainAdapter by lazy {
        VerticalMoviesAdapter(
            onItemClick = {movieItem ->
                val sheet = MovieDetailsBottomSheet(movieItem)
                sheet.show(supportFragmentManager, "MovieDetailsBottomSheet")
            }
        )
    }
    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupBannerRecycler()
        setupMainRecycler()
        observeMovies()
    }

    private fun setupMainRecycler() {
        binding.rvMainMovies.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvMainMovies.adapter = mainAdapter
        binding.rvMainMovies.setHasFixedSize(true)
        binding.rvMainMovies.isNestedScrollingEnabled = true
    }

    private fun setupBannerRecycler() {
        binding.rvBannerMovies.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvBannerMovies.adapter = bannerAdapter
        binding.rvBannerMovies.setHasFixedSize(true)
        binding.rvBannerMovies.isNestedScrollingEnabled = false


    }

    private fun observeMovies() {
        // OR if you're using Flow instead of LiveData:
        viewModel.fetchMovies()
        lifecycleScope.launch {
            viewModel.loading.collectLatest { loading ->
                if (loading) {
                    binding.loadingIndicator.visibility = View.VISIBLE
                } else {
                    binding.loadingIndicator.visibility = View.GONE
                }
            }
        }
        lifecycleScope.launch {
            viewModel.error.collectLatest { error ->
                if (error != null) {
                    binding.tvError.visibility = View.VISIBLE
                    binding.ivErrorLogo.visibility = View.VISIBLE
                    binding.tvError.text = error
                } else {
                    binding.tvError.visibility = View.GONE
                    binding.ivErrorLogo.visibility = View.GONE
                }
            }
        }
        lifecycleScope.launch {
            viewModel.movies.collectLatest { list ->
                val filtered = list.filter {
                    it.averageRating >= 8.9
                }

                Log.d("Movies_TAg", filtered.toString())
                bannerAdapter.submitList(filtered)
                mainAdapter.submitList(list)
            }
        }
    }
}