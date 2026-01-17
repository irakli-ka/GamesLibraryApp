package com.example.gameslibraryapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.gameslibraryapp.R
import com.example.gameslibraryapp.databinding.ItemGameCardRowBinding
import com.example.gameslibraryapp.model.Game

class GamesFeedAdapter(
    private val onGameClicked: (Game) -> Unit
) : PagingDataAdapter<Game, GamesFeedAdapter.GameViewHolder>(GameDiffCallback()) {

    inner class GameViewHolder(val binding: ItemGameCardRowBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        val binding = ItemGameCardRowBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        val viewHolder = GameViewHolder(binding)

        viewHolder.binding.root.setOnClickListener {
            val position = viewHolder.bindingAdapterPosition
            if (position != RecyclerView.NO_POSITION) {
                getItem(position)?.let { game ->
                    onGameClicked(game)
                }
            }
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        val game = getItem(position)
        val context = holder.itemView.context

        holder.binding.gameTitle.text = game?.name
        holder.binding.gameRating.text = game?.rating.toString()
        holder.binding.gameGenres.text = game?.genres?.joinToString { it.name }

        val ratingColor: Int
        if (game?.rating != null) {
            holder.binding.gameRating.text = game.rating.toString()
            ratingColor = when {
                game.rating >= 4.0 -> R.color.rating_high
                game.rating >= 2.5 -> R.color.rating_medium
                else -> R.color.rating_low
            }
        } else {
            holder.binding.gameRating.text = "N/A"
            ratingColor = R.color.rating_na
        }
        holder.binding.ratingBadge.setCardBackgroundColor(context.getColor(ratingColor))

        Glide.with(context)
            .load(game?.backgroundImage)
            .error(R.drawable.error)
            .into(holder.binding.gameImage)
    }
}

class GameDiffCallback : DiffUtil.ItemCallback<Game>() {
    override fun areItemsTheSame(oldItem: Game, newItem: Game): Boolean {
        return oldItem.id == newItem.id
    }
    override fun areContentsTheSame(oldItem: Game, newItem: Game): Boolean {
        return oldItem == newItem
    }
}
