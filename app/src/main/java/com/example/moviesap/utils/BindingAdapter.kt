package com.example.moviesap.utils

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.example.moviesap.R

@BindingAdapter("imageUrl")
fun loadImage(view: ImageView, url: String?) {
    if (!url.isNullOrEmpty()) {
        Glide.with(view.context)
            .load(url)
            .placeholder(R.drawable.test)
            .centerCrop()
            .into(view)
    }
}

@BindingAdapter("rating")
fun setRating(view: TextView, rating: Double?) {
    view.text = "⭐ ${rating ?: 0.0}"
}

@BindingAdapter("votes")
fun setVotes(view: TextView, votes: Int?) {
    val votesText = when {
        votes == null || votes == 0 -> "No votes"
        votes >= 1_000_000 -> "👍 ${String.format("%.1fM", votes / 1_000_000.0)} votes"
        votes >= 1_000 -> "👍 ${String.format("%.1fK", votes / 1_000.0)} votes"
        else -> "👍 $votes votes"
    }
    view.text = votesText
}