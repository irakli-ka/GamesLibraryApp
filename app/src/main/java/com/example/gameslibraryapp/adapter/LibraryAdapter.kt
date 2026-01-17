package com.example.gameslibraryapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.gameslibraryapp.R
import com.example.gameslibraryapp.databinding.ItemGameCardRowBinding
import com.example.gameslibraryapp.model.Game
import com.example.gameslibraryapp.viewmodel.AuthState

class LibraryAdapter(
    private val onGameClicked: (Game) -> Unit,
    private val onSaveGameClicked: (Game) -> Unit,
    private val onRemoveGameClicked: (Game) -> Unit,
    private val getLibraryIds: () -> Set<Int>,
    private val getAuthState: () -> AuthState
) : ListAdapter<Game, LibraryAdapter.GameViewHolder>(GameDiffCallback()) {

    inner class GameViewHolder(val binding: ItemGameCardRowBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        val binding = ItemGameCardRowBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return GameViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        val game = getItem(position) ?: return
        val context = holder.itemView.context

        with(holder.binding) {
            gameTitle.text = game.name

            Glide.with(context)
                .load(game.backgroundImage)
                .error(R.drawable.error)
                .centerCrop()
                .into(gameImage)

            gameRating.text = game.rating.toString()


            if (game.genres.isNotEmpty()) {
                gameGenres.text = game.genres.joinToString(", ") { it.name }
                gameGenres.visibility = View.VISIBLE
            } else {
                gameGenres.visibility = View.GONE
            }
            holder.binding.gameRating.text = game.rating.toString()
            val ratingColor: Int = when {
                game.rating >= 4.0 -> R.color.rating_high
                game.rating >= 2.5 -> R.color.rating_medium
                else -> R.color.rating_low
            }
            holder.binding.ratingBadge.setCardBackgroundColor(context.getColor(ratingColor))
        }

        val libraryIds = getLibraryIds()
        val authState = getAuthState()

        if (authState is AuthState.LoggedIn) {
            holder.binding.saveGameButton.visibility = View.VISIBLE
            if (libraryIds.contains(game.id)) {
                holder.binding.saveGameButton.setImageResource(R.drawable.ic_bookmark_filled)
                holder.binding.saveGameButton.setOnClickListener { onRemoveGameClicked(game) }
            } else {
                holder.binding.saveGameButton.setImageResource(R.drawable.ic_bookmark)
                holder.binding.saveGameButton.setOnClickListener { onSaveGameClicked(game) }
            }
        } else {
            holder.binding.saveGameButton.visibility = View.GONE
        }

        holder.binding.root.setOnClickListener {
            onGameClicked(game)
        }
    }
}