package com.example.gameslibraryapp

import MainViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
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
    private lateinit var gamesFeedAdapter: GamesFeedAdapter
    private val indicatorViews = mutableListOf<View>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.topBar.searchBar.setOnSearchListener(this)

        setupCarousel()
        setupGamesFeed()

        viewLifecycleOwner.lifecycleScope.launch {
            mainViewModel.gamesFeed.collectLatest { pagingData ->
                gamesFeedAdapter.submitData(pagingData)
            }
        }

        binding.gamesCarousel.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateLineIndicator(position)
            }
        })
    }

    private fun setupCarousel() {
        carouselAdapter = GamesCarouselAdapter(emptyList())
        binding.gamesCarousel.adapter = carouselAdapter
    }

    private fun setupGamesFeed() {
        gamesFeedAdapter = GamesFeedAdapter()

        gamesFeedAdapter.addLoadStateListener { loadState ->
            val isFirstLoad = gamesFeedAdapter.itemCount > 0 && carouselAdapter.itemCount == 0
            if (isFirstLoad) {
                val carouselGames = gamesFeedAdapter.snapshot().items.take(5)
                carouselAdapter.updateGames(carouselGames)
                setupLineIndicator(carouselGames.size)
            }
        }

        binding.gamesFeedRV.apply {
            adapter = gamesFeedAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun setupLineIndicator(count: Int) {
        binding.carouselIndicatorContainer.removeAllViews()
        indicatorViews.clear()

        if (count <= 0) return

        binding.carouselIndicatorContainer.weightSum = count.toFloat()
        for (i in 0 until count) {
            val view = View(requireContext())
            val params =
                LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f).apply {
                    marginEnd = 0
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

    override fun onSearch(query: String) {
        Toast.makeText(
            requireContext(),
            "MainFragment received search query: $query",
            Toast.LENGTH_LONG
        ).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}