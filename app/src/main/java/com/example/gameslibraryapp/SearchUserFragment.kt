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
import com.example.gameslibraryapp.adapter.LibraryAdapter
import com.example.gameslibraryapp.databinding.FragmentSearchUserBinding
import com.example.gameslibraryapp.viewmodel.MainViewModel
import kotlinx.coroutines.launch

class SearchUserFragment : Fragment() {

    private var _binding: FragmentSearchUserBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel: MainViewModel by activityViewModels()
    private lateinit var searchAdapter: LibraryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        binding.searchButton.setOnClickListener {
            val query = binding.searchEditText.text.toString().trim()
            if (query.isNotEmpty()) {
                mainViewModel.searchUserLibrary(query)
            }
        }

        mainViewModel.searchResultLibrary.observe(viewLifecycleOwner) { games ->
            searchAdapter.submitList(games)
        }

        mainViewModel.searchStatus.observe(viewLifecycleOwner) { status ->
            binding.statusText.text = status
            binding.statusText.visibility = if (status.isEmpty()) View.GONE else View.VISIBLE
        }

        viewLifecycleOwner.lifecycleScope.launch {
            mainViewModel.libraryGameIds.collect {
                searchAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun setupRecyclerView() {
        searchAdapter = LibraryAdapter(
            onGameClicked = { game ->
                val action = SearchUserFragmentDirections.actionSearchUserFragmentToGameDetailsFragment(game.id)
                findNavController().navigate(action)
            },
            onSaveGameClicked = { game ->
                mainViewModel.saveGameToLibrary(game)
                Toast.makeText(context, "Added to your library", Toast.LENGTH_SHORT).show()
            },
            onRemoveGameClicked = { game ->
                mainViewModel.removeGameFromLibrary(game.id)
                Toast.makeText(context, "Removed from your library", Toast.LENGTH_SHORT).show()
            },
            getLibraryIds = { mainViewModel.libraryGameIds.value },
            getAuthState = { mainViewModel.authState.value }
        )

        binding.searchRv.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = searchAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        mainViewModel.clearSearch()
    }
}