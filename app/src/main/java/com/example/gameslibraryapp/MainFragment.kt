package com.example.gameslibraryapp

import MainViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gameslibraryapp.adapter.CarouselHeaderAdapter
import com.example.gameslibraryapp.adapter.GamesCarouselAdapter
import com.example.gameslibraryapp.adapter.GamesFeedAdapter
import com.example.gameslibraryapp.databinding.FragmentMainBinding
import com.example.gameslibraryapp.views.SearchBarView
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainFragment : Fragment(), SearchBarView.OnSearchListener {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private val mainViewModel: MainViewModel by viewModels()

    private lateinit var carouselAdapter: GamesCarouselAdapter
    private lateinit var headerAdapter: CarouselHeaderAdapter
    private lateinit var gamesFeedAdapter: GamesFeedAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.topBar.searchBar.setOnSearchListener(this)
        setupRecyclerView()

        // 1. Observe the Carousel Data (Restores state when returning)
        viewLifecycleOwner.lifecycleScope.launch {
            mainViewModel.carouselGames.collectLatest { games ->
                if (games.isNotEmpty()) {
                    carouselAdapter.updateGames(games)

                    (binding.gamesFeedRV.adapter as? ConcatAdapter)?.adapters?.forEach {
                        if (it is CarouselHeaderAdapter) it.notifyItemChanged(0)
                    }
                }
            }
        }

        // 2. Observe the Feed
        viewLifecycleOwner.lifecycleScope.launch {
            mainViewModel.gamesFeed.collectLatest { pagingData ->
                gamesFeedAdapter.submitData(pagingData)
            }
        }
    }

    private fun setupRecyclerView() {
        carouselAdapter = GamesCarouselAdapter(emptyList())
        headerAdapter = CarouselHeaderAdapter(carouselAdapter)
        gamesFeedAdapter = GamesFeedAdapter()

        gamesFeedAdapter.addLoadStateListener { loadState ->
            if (!mainViewModel.hasCarouselData() &&
                gamesFeedAdapter.itemCount >= 5) {

                val carouselGames = gamesFeedAdapter.snapshot().items.take(5)
                mainViewModel.setCarouselGames(carouselGames)
            }
        }

        val concatAdapter = ConcatAdapter(headerAdapter, gamesFeedAdapter)

        binding.gamesFeedRV.apply {
            adapter = concatAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    override fun onSearch(query: String) {
        Toast.makeText(requireContext(), "Search: $query", Toast.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}