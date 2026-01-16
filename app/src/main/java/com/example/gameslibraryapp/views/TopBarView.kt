package com.example.gameslibraryapp.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.gameslibraryapp.R
import com.bumptech.glide.Glide
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

    fun setProfileImage(imageUrl: String?) {
        if (imageUrl.isNullOrEmpty()) {
            binding.profilePicture.setImageResource(R.drawable.ic_login)
        } else {
            Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.ic_person)
                .error(R.drawable.error)
                .into(binding.profilePicture)
        }
    }

    fun setOnProfileClickListener(listener: OnClickListener) {
        binding.profilePicture.setOnClickListener(listener)
    }
}