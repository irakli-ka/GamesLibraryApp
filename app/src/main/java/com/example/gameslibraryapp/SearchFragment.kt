package com.example.gameslibraryapp

import android.content.res.Resources
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.DatePicker
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.gameslibraryapp.databinding.FragmentSearchBinding
import com.example.gameslibraryapp.model.FilterableItem
import com.example.gameslibraryapp.viewmodel.AuthState
import com.example.gameslibraryapp.viewmodel.MainViewModel
import com.example.gameslibraryapp.viewmodel.SearchViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlinx.coroutines.launch


class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel: MainViewModel by activityViewModels()
    private val searchViewModel: SearchViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        searchViewModel.clearFilters()
        setupSpinners()
        setupDatePickers()
        mainViewModel.userProfile.observe(viewLifecycleOwner) { userProfile ->
            if (userProfile != null) {
                binding.topBar.setProfileImage(userProfile.profileImageUrl)
            }
        }

        mainViewModel.userProfile.observe(viewLifecycleOwner) { userProfile ->
            if (userProfile != null) {
                binding.topBar.setProfileImage(userProfile.profileImageUrl)
            }
        }

        mainViewModel.genres.observe(viewLifecycleOwner) { genresList ->
            populateChips(binding.chipGroupGenres, genresList)
        }

        mainViewModel.stores.observe(viewLifecycleOwner) { storesList ->
            populateChips(binding.chipGroupStores, storesList)
        }

        binding.applyFiltersBtn.setOnClickListener {
            val selectedFilters = buildFilterQuery()

            searchViewModel.applySearchFilters(selectedFilters)

            findNavController().navigate(R.id.action_searchFragment_to_searchresultFragment)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            mainViewModel.authState.collect { authState ->
                when (authState) {
                    is AuthState.LoggedIn -> {
                        binding.topBar.setOnProfileClickListener {
                            findNavController().navigate(R.id.action_global_profileFragment)
                        }
                        mainViewModel.userProfile.observe(viewLifecycleOwner) { profile ->
                            binding.topBar.setProfileImage(profile?.profileImageUrl)
                        }
                    }
                    is AuthState.LoggedOut -> {
                        binding.topBar.setOnProfileClickListener {
                            findNavController().navigate(R.id.action_global_loginFragment)
                        }
                        binding.topBar.setProfileImage(null)
                    }
                    is AuthState.Unknown -> {
                        binding.topBar.setOnProfileClickListener {
                            findNavController().navigate(R.id.action_global_loginFragment)
                        }
                    }
                }
            }
        }
    }


    private fun setupSpinners() {
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.ordering_options,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerOrdering.adapter = adapter
        }
    }

    private fun buildFilterQuery(): Map<String, String> {
        val filters = mutableMapOf<String, String>()

        val searchQuery = binding.topBar.searchBar.searchInput
        if (searchQuery.isNotBlank()) {
            filters["search"] = searchQuery
        }

        val orderingValues = resources.getStringArray(R.array.ordering_options)
        val selectedOrderingPosition = binding.spinnerOrdering.selectedItemPosition
        if (selectedOrderingPosition != -1) {
            filters["ordering"] = "-${orderingValues[selectedOrderingPosition]}"
        }

        val selectedGenreIds = binding.chipGroupGenres.checkedChipIds
            .joinToString(separator = ",")
        if (selectedGenreIds.isNotEmpty()) {
            filters["genres"] = selectedGenreIds
        }

        val selectedStoreIds = binding.chipGroupStores.checkedChipIds
            .joinToString(separator = ",")
        if (selectedStoreIds.isNotEmpty()) {
            filters["stores"] = selectedStoreIds
        }

        val fromYear = binding.dpDateFrom.year
        val toYear = binding.dpDateTo.year

        val startDate = "$fromYear-01-01"
        val endDate = "$toYear-12-31"
        filters["dates"] = "$startDate,$endDate"

        if (binding.switchSearchPrecise.isChecked) {
            filters["search_precise"] = "true"
        }

        if (binding.switchSearchExact.isChecked) {
            filters["search_exact"] = "true"
        }


        return filters
    }

    private fun <T : FilterableItem> populateChips(chipGroup: ChipGroup, items: List<T>) {
        chipGroup.removeAllViews()
        val inflater = LayoutInflater.from(chipGroup.context)

        for (item in items) {
            val chip = inflater.inflate(R.layout.item_filter_chip, chipGroup, false) as Chip

            chip.text = item.name
            chip.id = item.id

            chipGroup.addView(chip)
        }
    }

    private fun setupDatePickers() {
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val fromDatePicker = binding.dpDateFrom
        val toDatePicker = binding.dpDateTo

        fromDatePicker.maxDate = System.currentTimeMillis()
        toDatePicker.maxDate = System.currentTimeMillis()

        fromDatePicker.init(currentYear - 20, 0, 1, null)
        toDatePicker.init(currentYear, 0, 1, null)

        hideDayMonthSpinners(binding.dpDateFrom)
        hideDayMonthSpinners(binding.dpDateTo)
    }


    private fun hideDayMonthSpinners(datePicker: DatePicker) {
        try {
            val daySpinnerId =
                Resources.getSystem().getIdentifier("day", "id", "android")
            if (daySpinnerId != 0) {
                datePicker.findViewById<View>(daySpinnerId)?.visibility = View.GONE
            }

            val monthSpinnerId =
                Resources.getSystem().getIdentifier("month", "id", "android")
            if (monthSpinnerId != 0) {
                datePicker.findViewById<View>(monthSpinnerId)?.visibility = View.GONE
            }
        } catch (e: Exception) {
            Log.e("SearchFragment", "Error hiding date picker spinners", e)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}