package com.example.gameslibraryapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gameslibraryapp.adapter.CarouselHeaderAdapter
import com.example.gameslibraryapp.adapter.GamesCarouselAdapter
import com.example.gameslibraryapp.adapter.GamesFeedAdapter
import com.example.gameslibraryapp.databinding.FragmentMainBinding
import com.example.gameslibraryapp.viewmodel.AuthState
import com.example.gameslibraryapp.viewmodel.MainViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainFragment : Fragment() {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private val mainViewModel: MainViewModel by activityViewModels()
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

        binding.gamesFeedRV.alpha = 0f

        setupRecyclerView()


        binding.searchBarClickTarget.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_searchFragment)
        }

        mainViewModel.userProfile.observe(viewLifecycleOwner) { userProfile ->
            if (userProfile != null) {
                binding.topBar.setProfileImage(userProfile.profileImageUrl)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            mainViewModel.carouselGames.collectLatest { games ->
                if (games.isNotEmpty()) {
                    carouselAdapter.updateGames(games)
                    binding.gamesFeedRV.animate().alpha(1f).setDuration(300).start()
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

        viewLifecycleOwner.lifecycleScope.launch {
            mainViewModel.authState.flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .collect { authState ->
                    when (authState) {
                        is AuthState.LoggedIn -> {
                            binding.topBar.setOnProfileClickListener {
                                findNavController().navigate(R.id.action_global_profileFragment)
                            }
                            mainViewModel.userProfile.observe(viewLifecycleOwner) { profile ->
                                if (profile != null) {
                                    binding.topBar.setProfileImage(profile.profileImageUrl)
                                }
                            }
                        }

                        is AuthState.LoggedOut -> {
                            binding.topBar.setProfileImage(null)
                            binding.topBar.setOnProfileClickListener {
                                findNavController().navigate(R.id.action_global_loginFragment)
                            }
                        }

                        else -> {}
                    }
                }
        }
    }

    private fun setupRecyclerView() {
        carouselAdapter = GamesCarouselAdapter(
            games = emptyList(),
            onGameClicked = { clickedGame ->
                val action = MainFragmentDirections.actionMainFragmentToGameDetailsFragment(clickedGame.id)
                findNavController().navigate(action)
            },
            onSaveGameClicked = { gameToSave ->
                mainViewModel.saveGameToLibrary(gameToSave)
                Toast.makeText(context, "${gameToSave.name} added", Toast.LENGTH_SHORT).show()
            },
            onRemoveGameClicked = { gameToRemove ->
                mainViewModel.removeGameFromLibrary(gameToRemove.id)
                Toast.makeText(context, "${gameToRemove.name} removed", Toast.LENGTH_SHORT).show()
            },
            getLibraryIds = { mainViewModel.libraryGameIds.value.toList() },
            getAuthState = { mainViewModel.authState.value }
        )

        headerAdapter = CarouselHeaderAdapter(carouselAdapter, "Popular Games")

        gamesFeedAdapter = GamesFeedAdapter(
            onGameClicked = { clickedGame ->
                val action = MainFragmentDirections.actionMainFragmentToGameDetailsFragment(clickedGame.id)
                findNavController().navigate(action)
            },
            onSaveGameClicked = { gameToSave ->
                mainViewModel.saveGameToLibrary(gameToSave)
                Toast.makeText(context, "${gameToSave.name} added", Toast.LENGTH_SHORT).show()
            },
            onRemoveGameClicked = { gameToRemove ->
                mainViewModel.removeGameFromLibrary(gameToRemove.id)
                Toast.makeText(context, "${gameToRemove.name} removed", Toast.LENGTH_SHORT).show()
            },
            getLibraryIds = { mainViewModel.libraryGameIds.value },
            getAuthState = { mainViewModel.authState.value }
        )

        gamesFeedAdapter.addLoadStateListener { loadState ->
            if (!mainViewModel.hasCarouselData() && gamesFeedAdapter.itemCount >= 5) {
                val carouselGames = gamesFeedAdapter.snapshot().items.take(5)
                mainViewModel.setCarouselGames(carouselGames)
            }
        }

        val concatAdapter = ConcatAdapter(headerAdapter, gamesFeedAdapter)
        binding.gamesFeedRV.apply {
            adapter = concatAdapter
            layoutManager = LinearLayoutManager(context)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            mainViewModel.libraryGameIds.collect {
                gamesFeedAdapter.notifyDataSetChanged()
                carouselAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}