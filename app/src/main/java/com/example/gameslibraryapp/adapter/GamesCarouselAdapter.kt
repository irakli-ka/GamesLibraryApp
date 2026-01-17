package com.example.gameslibraryapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.gameslibraryapp.R
import com.example.gameslibraryapp.databinding.ItemGameCardBinding
import com.example.gameslibraryapp.model.Game
import com.example.gameslibraryapp.viewmodel.AuthState

class GamesCarouselAdapter(
    private var games: List<Game>,
    private val onGameClicked: (Game) -> Unit,
    private val onSaveGameClicked: (Game) -> Unit,
    private val onRemoveGameClicked: (Game) -> Unit,
    private val getLibraryIds: () -> List<Int>,
    private val getAuthState: () -> AuthState
) : RecyclerView.Adapter<GamesCarouselAdapter.GameViewHolder>() {

    inner class GameViewHolder(val binding: ItemGameCardBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        val binding = ItemGameCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        val viewHolder = GameViewHolder(binding)

        viewHolder.binding.root.setOnClickListener {
            val position = viewHolder.bindingAdapterPosition
            if (position != RecyclerView.NO_POSITION) {
                onGameClicked(games[position])
            }
        }
        return viewHolder
    }

    override fun getItemCount(): Int = games.size

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        val game = games[position]
        val context = holder.itemView.context

        holder.binding.gameTitle.text = game.name
        holder.binding.gameRating.text = String.format("%.2f⭐", game.rating)
        Glide.with(context)
            .load(game.backgroundImage)
            .into(holder.binding.gameImage)


        holder.binding.saveGameButton.setOnClickListener {
            onSaveGameClicked(game)
        }

        holder.binding.platformIconsContainer.removeAllViews()
        val addedIconTypes = mutableSetOf<Int>()
        game.platforms.forEach { platformInfo ->
            val platformName = platformInfo.platform.name.lowercase()

            val platformIconRes = when {
                platformName.contains("pc") -> R.drawable.ic_platform_pc
                platformName.contains("playstation") -> R.drawable.ic_platform_playstation
                platformName.contains("xbox") -> R.drawable.ic_platform_xbox
                platformName.contains("nintendo") -> R.drawable.ic_platform_nintendo
                else -> 0
            }

            if (platformIconRes != 0 && addedIconTypes.add(platformIconRes)) {
                val iconImageView = ImageView(context).apply {
                    setImageResource(platformIconRes)
                    layoutParams = LinearLayout.LayoutParams(
                        48,
                        48
                    ).also {
                        it.marginEnd = 8
                    }
                    setColorFilter(context.getColor(android.R.color.white))
                }
                holder.binding.platformIconsContainer.addView(iconImageView)
            }
            val libraryIds = getLibraryIds()
            val authState = getAuthState()

            if (authState is AuthState.LoggedIn) {
                holder.binding.saveGameButton.visibility = View.VISIBLE

                if (libraryIds.contains(game.id)) {
                    holder.binding.saveGameButton.setImageResource(R.drawable.ic_bookmark_filled)
                    holder.binding.saveGameButton.setOnClickListener {
                        onRemoveGameClicked(game)
                    }
                } else {
                    holder.binding.saveGameButton.setImageResource(R.drawable.ic_bookmark)
                    holder.binding.saveGameButton.setOnClickListener {
                        onSaveGameClicked(game)
                    }
                }
            } else {
                holder.binding.saveGameButton.visibility = View.GONE
            }
        }
    }
    fun updateGames(newGames: List<Game>) {
        this.games = newGames
        notifyDataSetChanged()
    }

}