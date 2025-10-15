package com.example.moviesap.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.moviesap.data.models.MovieItem
import com.example.moviesap.databinding.BannerItemLayoutBinding
import com.example.moviesap.utils.truncate


class BannerMoviesAdapter(
    private val onItemClick: (MovieItem) -> Unit
) :
    ListAdapter<MovieItem, BannerMoviesAdapter.MovieViewHolder>(DiffCallback()) {

    inner class MovieViewHolder(private val binding: BannerItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(movie: MovieItem) {
            binding.tvMovieName.text = (movie.primaryTitle ?: "Unknown").truncate(20)
            binding.tvMovieRate.text = "⭐ ${movie.averageRating ?: "N/A"}"

            Glide.with(binding.ivBanner.context)
                .load(movie.primaryImage)
                .centerCrop()
                .transform(RoundedCorners(24))
                .into(binding.ivBanner)

            binding.root.setOnClickListener {
                onItemClick(movie)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = BannerItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return MovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<MovieItem>() {
        override fun areItemsTheSame(oldItem: MovieItem, newItem: MovieItem): Boolean {
            return oldItem.id == newItem.id // or unique identifier
        }

        override fun areContentsTheSame(oldItem: MovieItem, newItem: MovieItem): Boolean {
            return oldItem == newItem
        }
    }
}

