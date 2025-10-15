package com.example.moviesap.ui.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moviesap.databinding.ActivityMainBinding
import com.example.moviesap.ui.adapter.BannerMoviesAdapter
import com.example.moviesap.ui.adapter.VerticalMoviesAdapter
import com.example.moviesap.ui.fragments.MovieDetailsBottomSheet
import com.example.moviesap.ui.viewmodel.MoviesViewModel
import com.example.moviesap.ui.viewmodel.SharedMovieViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // Hilt will automatically inject dependencies
    private val viewModel: MoviesViewModel by viewModels()
    private val sharedViewModel: SharedMovieViewModel by viewModels()

    private val bannerAdapter by lazy {
        BannerMoviesAdapter(
            onItemClick = { movieItem ->
                sharedViewModel.setMovie(movieItem)
                MovieDetailsBottomSheet().show(supportFragmentManager, "MovieDetailsBottomSheet")
            }
        )
    }

    private val mainAdapter by lazy {
        VerticalMoviesAdapter(
            onItemClick = { movieItem ->
                sharedViewModel.setMovie(movieItem)
                MovieDetailsBottomSheet().show(supportFragmentManager, "MovieDetailsBottomSheet")
            }
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBannerRecycler()
        setupMainRecycler()
        setupSwipeToRefresh()
        observeMovies()
    }

    private fun setupSwipeToRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            lifecycleScope.launch {
                viewModel.refreshMoviesFromApi()
                binding.swipeRefreshLayout.isRefreshing = false
            }
        }
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
        viewModel.fetchMovies()

        // Observe loading state
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.loading.collect { loading ->
                    binding.loadingIndicator.visibility = if (loading) View.VISIBLE else View.GONE
                }
            }
        }

        // Observe error state
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.error.collect { error ->
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
        }

        // Observe movies list
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.movies.collect { list ->
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
}