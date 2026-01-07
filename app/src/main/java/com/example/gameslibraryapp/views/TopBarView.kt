package com.example.gameslibraryapp.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.gameslibraryapp.views.SearchBarView
import com.example.gameslibraryapp.databinding.TopBarViewBinding

class TopBarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding: TopBarViewBinding =
        TopBarViewBinding.inflate(LayoutInflater.from(context), this, true)


    val searchBar: SearchBarView
        get() = binding.searchBar

    init {
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.profilePicture.setOnClickListener {
            Toast.makeText(context, "Menu button clicked!", Toast.LENGTH_SHORT).show()
        }
    }
}