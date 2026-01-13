package com.example.gameslibraryapp

import MainViewModel
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.launch
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gameslibraryapp.adapter.GamesFeedAdapter
import com.example.gameslibraryapp.databinding.FragmentSearchresultBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class SearchresultFragment : Fragment() {

    private var _binding: FragmentSearchresultBinding? = null
    private val binding get() = _binding!!

    // Use the shared activity-scoped ViewModel
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

        // 1. Setup the adapter and RecyclerView
        setupRecyclerView()

        // 2. Collect the latest PagingData from the shared ViewModel
        viewLifecycleOwner.lifecycleScope.launch {
            mainViewModel.gamesFeed.collectLatest { pagingData ->
                resultsAdapter.submitData(pagingData)
            }
        }
    }

    private fun setupRecyclerView() {
        resultsAdapter = GamesFeedAdapter()
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