package com.example.moviesap.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.moviesap.R
import com.example.moviesap.data.models.MovieItem
import com.example.moviesap.databinding.MovieItemLayoutBinding


class VerticalMoviesAdapter(
    private val onItemClick: (MovieItem) -> Unit

) : ListAdapter<MovieItem, VerticalMoviesAdapter.MovieViewHolder>(DiffCallback()) {

    inner class MovieViewHolder(private val binding: MovieItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(movie: MovieItem) {
            binding.apply {
                tvMovieName.text = movie.primaryTitle
                tvDescription.text = movie.description
                tvAverageRating.text = "⭐ ${movie.averageRating}"
                tvRatedNumber.text = "👍 ${movie.numVotes}"

                // Load image with rounded corners using Glide
                Glide.with(ivMovie.context)
                    .load(movie.primaryImage)
                    .centerCrop()     // or .fitCenter() or .circleCrop()
                    .placeholder(R.drawable.test)
                    .into(binding.ivMovie)

                root.setOnClickListener {
                    onItemClick(movie)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = MovieItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<MovieItem>() {
        override fun areItemsTheSame(oldItem: MovieItem, newItem: MovieItem): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: MovieItem, newItem: MovieItem): Boolean =
            oldItem == newItem
    }
}
