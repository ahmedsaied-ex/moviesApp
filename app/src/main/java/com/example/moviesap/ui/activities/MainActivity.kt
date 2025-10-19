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
import com.example.moviesap.data.models.MovieItem
import com.example.moviesap.databinding.ActivityMainBinding
import com.example.moviesap.ui.adapter.BannerMoviesAdapter
import com.example.moviesap.ui.adapter.VerticalMoviesAdapter
import com.example.moviesap.ui.fragments.MovieDetailsBottomSheet
import com.example.moviesap.ui.states.UiState
import com.example.moviesap.ui.viewmodel.MoviesViewModel
import com.example.moviesap.ui.viewmodel.SharedMovieViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MoviesViewModel by viewModels()
    private val sharedViewModel: SharedMovieViewModel by viewModels()

    private val bannerAdapter by lazy {
        BannerMoviesAdapter(onItemClick = { openBottomSheet(it) })
    }

    private val mainAdapter by lazy {
        VerticalMoviesAdapter(onItemClick = { openBottomSheet(it) })
    }

    private fun openBottomSheet(movieItem: MovieItem) {
        sharedViewModel.setMovie(movieItem)
        MovieDetailsBottomSheet().show(supportFragmentManager, "MovieDetailsBottomSheet")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBannerRecycler()
        setupMainRecycler()
        setupSwipeToRefresh()
        observeUiState()

        viewModel.fetchMovies()
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
        binding.rvMainMovies.apply {
            layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)
            adapter = mainAdapter
            setHasFixedSize(true)
            isNestedScrollingEnabled = true
        }
    }

    private fun setupBannerRecycler() {
        binding.rvBannerMovies.apply {
            layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = bannerAdapter
            setHasFixedSize(true)
            isNestedScrollingEnabled = false
        }
    }

    private fun observeUiState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is UiState.Loading -> showLoading()
                        is UiState.Success -> showMovies(state.movies)
                        is UiState.Error -> showError(state.message)
                    }
                }
            }
        }
    }

    private fun showLoading() {
        binding.loadingIndicator.visibility = View.VISIBLE
        binding.tvError.visibility = View.GONE
        binding.ivErrorLogo.visibility = View.GONE
    }

    private fun showMovies(list: List<MovieItem>) {
        binding.loadingIndicator.visibility = View.GONE
        binding.tvError.visibility = View.GONE
        binding.ivErrorLogo.visibility = View.GONE

        val filtered = list.filter { it.averageRating >= 8.9 }
        Log.d("Movies_TAG", filtered.toString())

        bannerAdapter.submitList(filtered)
        mainAdapter.submitList(list)
    }

    private fun showError(message: String) {
        binding.loadingIndicator.visibility = View.GONE
        binding.tvError.visibility = View.VISIBLE
        binding.ivErrorLogo.visibility = View.VISIBLE
        binding.tvError.text = message
    }
}
