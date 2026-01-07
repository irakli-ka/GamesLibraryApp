package com.example.gameslibraryapp
import com.example.gameslibraryapp.viewmodel.MainViewModel
import com.example.gameslibraryapp.adapter.GamesCarouselAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.example.gameslibraryapp.databinding.FragmentMainBinding
import com.example.gameslibraryapp.views.SearchBarView


class MainFragment : Fragment(), SearchBarView.OnSearchListener {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private val mainViewModel: MainViewModel by viewModels()
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


        val carouselAdapter = GamesCarouselAdapter(emptyList())
        binding.gamesCarousel.adapter = carouselAdapter

        // 2. Observe the LiveData from the ViewModel
        mainViewModel.games.observe(viewLifecycleOwner) { gameList ->
            // When data is received, update the adapter's list
            carouselAdapter.updateGames(gameList)
            // And setup/update the indicators
            setupLineIndicator(gameList.size)
            updateLineIndicator(binding.gamesCarousel.currentItem)
        }

        mainViewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            // Show an error message if something goes wrong
            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
        }

        // 3. Trigger the network request
        mainViewModel.fetchGames()

        // Listen for page change events
        binding.gamesCarousel.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateLineIndicator(position)
            }
        })


        binding.topBar.searchBar.setOnSearchListener(this)
    }



    private fun setupLineIndicator(count: Int) {
        binding.carouselIndicatorContainer.removeAllViews()
        indicatorViews.clear()
        binding.carouselIndicatorContainer.weightSum = count.toFloat()
        for (i in 0 until count) {
            val view = View(requireContext())
            val params = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f).apply {
                marginEnd = 0

            }
            view.layoutParams = params
            view.setBackgroundResource(if (i == 0) R.drawable.indicator_active_segment else R.drawable.indicator_inactive_segment)
            indicatorViews.add(view)
            binding.carouselIndicatorContainer.addView(view)
        }
    }

    private fun updateLineIndicator(position: Int) {
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