package com.example.moviesap.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.moviesap.R
import com.example.moviesap.data.models.MovieItem
import com.example.moviesap.databinding.FragmentMovieDetailsBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class MovieDetailsBottomSheet(
    private val movie: MovieItem
) : BottomSheetDialogFragment() {

    private var _binding: FragmentMovieDetailsBottomSheetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMovieDetailsBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()

        // Make sure the sheet expands fully
        val bottomSheet = (dialog as? BottomSheetDialog)
            ?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        bottomSheet?.let {
            val behavior = BottomSheetBehavior.from(it)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.skipCollapsed = true
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindData()
    }

    private fun bindData() {
        binding.apply {
            tvMovieTitle.text = movie.primaryTitle
            tvMovieDescription.text = movie.description
            tvRating.text = "⭐ ${movie.averageRating}"
            tvVotes.text = "👍 ${movie.numVotes}"

            Glide.with(ivMoviePoster.context)
                .load(movie.primaryImage)
                .centerCrop()
                .placeholder(R.drawable.test)
                .into(ivMoviePoster)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
