package com.example.moviesap.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.moviesap.data.models.MovieItem
import com.example.moviesap.databinding.ItemBannerBinding
import com.example.moviesap.databinding.MovieItemLayoutBinding
import com.example.moviesap.ui.MovieListItem

class UnifiedMoviesAdapter(
    private val onItemClick: (MovieItem) -> Unit
) : ListAdapter<MovieListItem, RecyclerView.ViewHolder>(MovieListDiffCallback()) {

    companion object {
        private const val VIEW_TYPE_BANNER = 0
        private const val VIEW_TYPE_MOVIE = 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is MovieListItem.BannerItem -> VIEW_TYPE_BANNER
            is MovieListItem.RegularItem -> VIEW_TYPE_MOVIE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_BANNER -> {
                val binding = ItemBannerBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                BannerViewHolder(binding)
            }
            else -> {
                val binding = MovieItemLayoutBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                MovieViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is MovieListItem.BannerItem -> (holder as BannerViewHolder).bind(item.movies)
            is MovieListItem.RegularItem -> (holder as MovieViewHolder).bind(item.movie)
        }
    }

    inner class BannerViewHolder(private val binding: ItemBannerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val bannerAdapter = BannerMoviesAdapter(onItemClick)

        init {
            binding.rvBanner.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = bannerAdapter
            }
        }

        fun bind(movies: List<MovieItem>) {
            bannerAdapter.submitList(movies)
        }
    }

    inner class MovieViewHolder(private val binding: MovieItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(movie: MovieItem) {
            binding.movie = movie
            binding.root.setOnClickListener { onItemClick(movie) }
        }
    }

    class MovieListDiffCallback : DiffUtil.ItemCallback<MovieListItem>() {
        override fun areItemsTheSame(oldItem: MovieListItem, newItem: MovieListItem): Boolean {
            return when {
                oldItem is MovieListItem.BannerItem && newItem is MovieListItem.BannerItem -> true
                oldItem is MovieListItem.RegularItem && newItem is MovieListItem.RegularItem ->
                    oldItem.movie.id == newItem.movie.id
                else -> false
            }
        }

        override fun areContentsTheSame(oldItem: MovieListItem, newItem: MovieListItem): Boolean {
            return oldItem == newItem
        }
    }
}