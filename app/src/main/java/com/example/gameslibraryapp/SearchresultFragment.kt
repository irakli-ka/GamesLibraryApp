package com.example.gameslibraryapp

import MainViewModel
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.launch
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gameslibraryapp.adapter.GamesFeedAdapter
import com.example.gameslibraryapp.databinding.FragmentSearchresultBinding
import com.example.gameslibraryapp.viewmodel.SearchViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class SearchresultFragment : Fragment() {

    private var _binding: FragmentSearchresultBinding? = null
    private val binding get() = _binding!!
    private val searchViewModel: SearchViewModel by activityViewModels()
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
        resultsAdapter = GamesFeedAdapter { clickedGame ->
            val action =
                SearchresultFragmentDirections.actionSearchresultFragmentToGameDetailsFragment(clickedGame.id)
            findNavController().navigate(action)
        }

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