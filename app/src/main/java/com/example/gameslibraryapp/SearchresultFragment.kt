package com.example.gameslibraryapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gameslibraryapp.adapter.GamesFeedAdapter
import com.example.gameslibraryapp.databinding.FragmentSearchresultBinding
import com.example.gameslibraryapp.viewmodel.MainViewModel
import com.example.gameslibraryapp.viewmodel.SearchViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class SearchresultFragment : Fragment() {

    private var _binding: FragmentSearchresultBinding? = null
    private val binding get() = _binding!!
    private val searchViewModel: SearchViewModel by activityViewModels()
    private val mainViewModel: MainViewModel by activityViewModels()
    private lateinit var resultsAdapter: GamesFeedAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchresultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()

        viewLifecycleOwner.lifecycleScope.launch {
            searchViewModel.searchResults.collectLatest { pagingData ->
                resultsAdapter.submitData(pagingData)
            }
        }
    }

    private fun setupRecyclerView() {
        resultsAdapter = GamesFeedAdapter(
            onGameClicked = { clickedGame ->
                val action =
                    SearchresultFragmentDirections.actionSearchresultFragmentToGameDetailsFragment(
                        clickedGame.id
                    )
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


        binding.searchResultsRv.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = resultsAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}