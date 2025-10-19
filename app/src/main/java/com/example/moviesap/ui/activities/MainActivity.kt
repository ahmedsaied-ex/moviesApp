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
import androidx.recyclerview.widget.RecyclerView
import com.example.moviesap.data.models.MovieItem
import com.example.moviesap.databinding.ActivityMainBinding
import com.example.moviesap.ui.MovieListItem
import com.example.moviesap.ui.adapter.UnifiedMoviesAdapter
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

    private val mainAdapter by lazy {
        UnifiedMoviesAdapter(onItemClick = { openBottomSheet(it) })
    }

    private var isLoadingMore = false

    private fun openBottomSheet(movieItem: MovieItem) {
        sharedViewModel.setMovie(movieItem)
        MovieDetailsBottomSheet().show(supportFragmentManager, "MovieDetailsBottomSheet")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupMainRecycler()
        setupSwipeToRefresh()
        setupPaginationListener()
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
            layoutManager = LinearLayoutManager(
                this@MainActivity,
                LinearLayoutManager.VERTICAL,
                false
            )
            adapter = mainAdapter
            setHasFixedSize(false)
        }
    }

    private fun setupPaginationListener() {
        binding.rvMainMovies.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                // Load more when user is 5 items away from the bottom
                if (!isLoadingMore &&
                    (visibleItemCount + firstVisibleItemPosition) >= totalItemCount - 5 &&
                    firstVisibleItemPosition >= 0 &&
                    totalItemCount > 0) {

                    Log.d("MainActivity", "Loading more movies...")
                    viewModel.loadMoreMovies()
                }
            }
        })
    }

    private fun observeUiState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is UiState.Loading -> showLoading()
                        is UiState.Success -> showMovies(state.movies, state.hasMore)
                        is UiState.LoadingMore -> showLoadingMore()
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
        isLoadingMore = false
    }

    private fun showLoadingMore() {
        binding.loadingIndicator.visibility = View.GONE
        binding.tvError.visibility = View.GONE
        binding.ivErrorLogo.visibility = View.GONE
        isLoadingMore = true

        Log.d("MainActivity", "Loading more state activated")
    }

    private fun showMovies(list: List<MovieItem>, hasMore: Boolean) {
        binding.loadingIndicator.visibility = View.GONE
        binding.tvError.visibility = View.GONE
        binding.ivErrorLogo.visibility = View.GONE
        isLoadingMore = false

        // Filter high-rated movies for banner
        val filtered = list.filter { it.averageRating >= 8.9 }

        Log.d("MainActivity", "Displayed: ${list.size}, Banner: ${filtered.size}, HasMore: $hasMore")

        // Create unified list with banner at top and movies below
        val unifiedList = mutableListOf<MovieListItem>()

        // Add banner as first item
        unifiedList.add(MovieListItem.BannerItem(filtered))

        // Add all movies as regular items
        unifiedList.addAll(list.map { MovieListItem.RegularItem(it) })

        // Submit the unified list to adapter
        mainAdapter.submitList(unifiedList)
    }

    private fun showError(message: String) {
        binding.loadingIndicator.visibility = View.GONE
        binding.tvError.visibility = View.VISIBLE
        binding.ivErrorLogo.visibility = View.VISIBLE
        binding.tvError.text = message
        isLoadingMore = false
    }
}