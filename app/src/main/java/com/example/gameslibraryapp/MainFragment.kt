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
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainFragment : Fragment(), SearchBarView.OnSearchListener {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private val mainViewModel: MainViewModel by viewModels()
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var carouselAdapter: GamesCarouselAdapter
    private lateinit var headerAdapter: CarouselHeaderAdapter
    private lateinit var gamesFeedAdapter: GamesFeedAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        auth = Firebase.auth
        database = Firebase.database
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.topBar.searchBar.setOnSearchListener(this)
        setupRecyclerView()

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

        viewLifecycleOwner.lifecycleScope.launch {
            mainViewModel.gamesFeed.collectLatest { pagingData ->
                gamesFeedAdapter.submitData(pagingData)
            }
        }

        if (auth.currentUser != null) {
            val userEmail = auth.currentUser!!.email
            if (userEmail != null) {
                database.reference.child("username_to_email")
                    .orderByValue()
                    .equalTo(userEmail)
                    .get()
                    .addOnSuccessListener { dataSnapshot ->
                        if (dataSnapshot.exists()) {
                            val username = dataSnapshot.children.first().key
                            if (username != null) {
                                Toast.makeText(context, "Welcome, $username!", Toast.LENGTH_SHORT)
                                    .show()
                                val imageUrl =
                                    "https://api.dicebear.com/8.x/pixel-art/png?seed=$username"
                                binding.topBar.setProfileImage(imageUrl)
                            }
                        } else {
                            binding.topBar.setProfileImage(null)
                        }
                    }.addOnFailureListener {
                        Toast.makeText(
                            context,
                            "Error connecting to the database.",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        binding.topBar.setProfileImage(null)
                    }
            } else {
                binding.topBar.setProfileImage(null)
            }
        }


    }

    private fun setupRecyclerView() {
        carouselAdapter = GamesCarouselAdapter(emptyList())
        headerAdapter = CarouselHeaderAdapter(carouselAdapter)
        gamesFeedAdapter = GamesFeedAdapter()

        gamesFeedAdapter.addLoadStateListener { loadState ->
            if (!mainViewModel.hasCarouselData() &&
                gamesFeedAdapter.itemCount >= 5
            ) {

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