package com.example.gameslibraryapp.views

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import com.example.gameslibraryapp.databinding.SearchBarViewBinding

class SearchBarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    interface OnSearchListener {
        fun onSearch(query: String)
    }

    private var searchListener: OnSearchListener? = null

    fun setOnSearchListener(listener: OnSearchListener) {
        this.searchListener = listener
    }

    private val binding: SearchBarViewBinding =
        SearchBarViewBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
        setupListeners()
    }

    private fun performSearch() {
        val query = binding.searchInput.text.toString()
        searchListener?.onSearch(query)
    }

    private fun setupListeners() {
        binding.searchInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch()
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
    }
}