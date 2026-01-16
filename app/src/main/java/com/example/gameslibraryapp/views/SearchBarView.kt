package com.example.gameslibraryapp.views

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.example.gameslibraryapp.databinding.SearchBarViewBinding

class SearchBarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {


    private val binding: SearchBarViewBinding =
        SearchBarViewBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
    }

    val searchInput: String
        get() = binding.searchInput.text.toString()

}

