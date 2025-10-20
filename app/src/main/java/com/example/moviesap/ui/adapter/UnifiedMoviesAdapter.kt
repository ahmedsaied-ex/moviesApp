package com.example.moviesap.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.moviesap.data.models.MovieItem
import com.example.moviesap.data.models.MovieListItem
import com.example.moviesap.databinding.ItemBannerContainerBinding
import com.example.moviesap.databinding.MovieItemLayoutBinding

class UnifiedMoviesAdapter(
    private val onItemClick: (MovieItem) -> Unit
) : ListAdapter<MovieListItem, RecyclerView.ViewHolder>(MovieListDiffCallback()) {

    companion object {
        private const val VIEW_TYPE_BANNER = 0
        private const val VIEW_TYPE_VERTICAL = 1
        private const val VIEW_TYPE_LOADING = 2
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is MovieListItem.BannerItem -> VIEW_TYPE_BANNER
            is MovieListItem.VerticalItem -> VIEW_TYPE_VERTICAL
            is MovieListItem.LoadingItem -> VIEW_TYPE_LOADING
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_BANNER -> {
                val binding = ItemBannerContainerBinding.inflate(inflater, parent, false)
                BannerContainerViewHolder(binding)
            }
            VIEW_TYPE_VERTICAL -> {
                val binding = MovieItemLayoutBinding.inflate(inflater, parent, false)
                VerticalViewHolder(binding)
            }
            VIEW_TYPE_LOADING -> {
                // You can create a loading layout if needed
                val binding = MovieItemLayoutBinding.inflate(inflater, parent, false)
                LoadingViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Invalid view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is MovieListItem.BannerItem -> (holder as BannerContainerViewHolder).bind(item.movies)
            is MovieListItem.VerticalItem -> (holder as VerticalViewHolder).bind(item.movie)
            is MovieListItem.LoadingItem -> {} // Loading doesn't need binding
        }
    }

    // This ViewHolder contains the horizontal RecyclerView for banners
    inner class BannerContainerViewHolder(private val binding: ItemBannerContainerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val bannerAdapter = BannerMoviesAdapter(onItemClick)

        init {
            // Setup the horizontal RecyclerView
            binding.rvBannerMovies.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = bannerAdapter
                setHasFixedSize(true)
                // Disable nested scrolling for better performance
                isNestedScrollingEnabled = false
            }
        }

        fun bind(movies: List<MovieItem>) {
            bannerAdapter.submitList(movies)
        }
    }

    inner class VerticalViewHolder(private val binding: MovieItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(movie: MovieItem) {
            binding.movie = movie
            binding.executePendingBindings()
            binding.root.setOnClickListener { onItemClick(movie) }
        }
    }

    inner class LoadingViewHolder(binding: MovieItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    class MovieListDiffCallback : DiffUtil.ItemCallback<MovieListItem>() {
        override fun areItemsTheSame(oldItem: MovieListItem, newItem: MovieListItem): Boolean {
            return when {
                oldItem is MovieListItem.BannerItem && newItem is MovieListItem.BannerItem -> true
                oldItem is MovieListItem.VerticalItem && newItem is MovieListItem.VerticalItem ->
                    oldItem.movie.id == newItem.movie.id
                oldItem is MovieListItem.LoadingItem && newItem is MovieListItem.LoadingItem -> true
                else -> false
            }
        }

        override fun areContentsTheSame(oldItem: MovieListItem, newItem: MovieListItem): Boolean {
            return oldItem == newItem
        }
    }
}