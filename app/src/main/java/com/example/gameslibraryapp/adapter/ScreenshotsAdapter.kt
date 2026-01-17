package com.example.gameslibraryapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.gameslibraryapp.databinding.ItemGameDetailsCarouselBinding


class ScreenshotsAdapter(
    private val imageUrls: List<String>
) : RecyclerView.Adapter<ScreenshotsAdapter.ScreenshotViewHolder>() {

    inner class ScreenshotViewHolder(val binding: ItemGameDetailsCarouselBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScreenshotViewHolder {
        val binding = ItemGameDetailsCarouselBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ScreenshotViewHolder(binding)
    }

    override fun getItemCount(): Int = imageUrls.size

    override fun onBindViewHolder(holder: ScreenshotViewHolder, position: Int) {
        val imageUrl = imageUrls[position]
        Glide.with(holder.itemView.context)
            .load(imageUrl)
            .into(holder.binding.gameImage)
    }
}