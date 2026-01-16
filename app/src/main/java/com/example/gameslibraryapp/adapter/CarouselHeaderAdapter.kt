package com.example.gameslibraryapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.gameslibraryapp.R
import com.example.gameslibraryapp.databinding.LayoutCarouselHeaderBinding

class CarouselHeaderAdapter(
    private val carouselAdapter: GamesCarouselAdapter
) : RecyclerView.Adapter<CarouselHeaderAdapter.HeaderViewHolder>() {

    inner class HeaderViewHolder(val binding: LayoutCarouselHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val indicatorViews = mutableListOf<View>()

        fun bind() {
            binding.gamesCarousel.adapter = carouselAdapter

            val count = carouselAdapter.itemCount
            setupLineIndicator(count)

            binding.gamesCarousel.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    updateLineIndicator(position)
                }
            })
        }

        private fun setupLineIndicator(count: Int) {
            binding.carouselIndicatorContainer.removeAllViews()
            indicatorViews.clear()
            if (count <= 0) return

            binding.carouselIndicatorContainer.weightSum = count.toFloat()
            for (i in 0 until count) {
                val view = View(binding.root.context)
                val params = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f).apply {
                    marginEnd = 8
                }
                view.layoutParams = params
                view.setBackgroundResource(if (i == 0) R.drawable.indicator_active_segment else R.drawable.indicator_inactive_segment)
                indicatorViews.add(view)
                binding.carouselIndicatorContainer.addView(view)
            }
        }

        private fun updateLineIndicator(position: Int) {
            if (position < 0 || position >= indicatorViews.size) return
            for (i in indicatorViews.indices) {
                indicatorViews[i].setBackgroundResource(
                    if (i == position) R.drawable.indicator_active_segment else R.drawable.indicator_inactive_segment
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeaderViewHolder {
        val binding = LayoutCarouselHeaderBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return HeaderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HeaderViewHolder, position: Int) {
        holder.bind()
    }

    override fun getItemCount(): Int = 1
}