package com.example.gameslibraryapp

import GameDetailsViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gameslibraryapp.adapter.CarouselHeaderAdapter
import com.example.gameslibraryapp.adapter.ScreenshotsAdapter
import com.example.gameslibraryapp.databinding.FragmentGameDetailsBinding
import com.example.gameslibraryapp.model.GameDetails
import com.example.gameslibraryapp.model.ShortScreenshot
import com.google.android.material.chip.Chip
import kotlin.getValue

class GameDetailsFragment : Fragment() {

    private var _binding: FragmentGameDetailsBinding? = null
    private val binding get() = _binding!!

    private val args: GameDetailsFragmentArgs by navArgs()
    private val detailsViewModel: GameDetailsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val gameId = args.gameId
        detailsViewModel.fetchGameDetails(gameId)

        binding.detailsHeaderContainer.layoutManager = LinearLayoutManager(context)

        detailsViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.detailsProgressBar.isVisible = isLoading
            binding.detailsHeaderContainer.isVisible = !isLoading
            binding.gameDetailTitle.isVisible = !isLoading
            binding.infoLayout.isVisible = !isLoading
            binding.gameDetailDescription.isVisible = !isLoading
            binding.genreChips.isVisible = !isLoading
            binding.platformsLabel.isVisible = !isLoading
            binding.platformChips.isVisible = !isLoading
            binding.storesLabel.isVisible = !isLoading
            binding.storeChips.isVisible = !isLoading
            binding.tagsLabel.isVisible = !isLoading
            binding.tagChips.isVisible = !isLoading
            binding.additionalInfoLayout.isVisible = !isLoading
            binding.descriptionLabel.isVisible = !isLoading
        }

        var gameDetails: GameDetails? = null
        var screenshots: List<ShortScreenshot>? = null

        detailsViewModel.gameDetails.observe(viewLifecycleOwner) { details ->
            gameDetails = details
            details?.let {
                binding.gameDetailTitle.text = it.name
                binding.gameDetailDeveloper.text = "Creator: ${it.developers?.joinToString { dev -> dev.name }}"
                binding.gameDetailRating.text = "Rating: ${it.rating} ⭐"
                binding.releaseDate.text = "Released: ${it.released}"
                binding.website.text = "Website: ${it.website}"
                binding.gameDetailDescription.text = it.descriptionRaw

                populateChipGroup(binding.genreChips, it.genres?.map { g -> g.name })
                populateChipGroup(binding.platformChips, it.platforms?.map { p -> p.platform.name })
                populateChipGroup(binding.storeChips, it.stores?.map { s -> s.store.name })
                populateChipGroup(binding.tagChips, it.tags?.map { t -> t.name })
            }

            if (screenshots != null) {
                setupCarousel(gameDetails, screenshots)
            }
        }
        detailsViewModel.screenshots.observe(viewLifecycleOwner) { screenshotList ->
            screenshots = screenshotList
            if (gameDetails != null) {
                setupCarousel(gameDetails, screenshots)
            }
        }
    }

    private fun populateChipGroup(chipGroup: com.google.android.material.chip.ChipGroup, items: List<String>?) {
        val inflater = LayoutInflater.from(chipGroup.context)

        items?.forEach { itemText ->
            val chip = inflater.inflate(R.layout.item_filter_chip, chipGroup, false) as Chip
            chip.text = itemText
            chip.isClickable = false
            chipGroup.addView(chip)
        }
    }

    private fun setupCarousel(gameDetails: GameDetails?, screenshots: List<ShortScreenshot>?) {
        if (gameDetails == null || screenshots == null) return

        val fullImageList = mutableListOf<String>()
        gameDetails.backgroundImage?.let {
            fullImageList.add(it)
        }
        fullImageList.addAll(screenshots.map { it.image })

        if (fullImageList.isNotEmpty()) {
            val screenshotsAdapter = ScreenshotsAdapter(fullImageList)
            val headerAdapter = CarouselHeaderAdapter(screenshotsAdapter)
            binding.detailsHeaderContainer.adapter = headerAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}