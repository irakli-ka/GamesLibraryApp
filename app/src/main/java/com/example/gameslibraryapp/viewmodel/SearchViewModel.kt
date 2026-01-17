package com.example.gameslibraryapp.viewmodel

import GamePagingSource
import RetrofitInstance
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.gameslibraryapp.model.Game
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest

class SearchViewModel : ViewModel() {

    private val _searchFilters = MutableStateFlow<Map<String, String?>>(emptyMap())

    val searchResults: Flow<PagingData<Game>> = _searchFilters.flatMapLatest { filters ->
        Pager(
            config = PagingConfig(pageSize = 20),
            pagingSourceFactory = { GamePagingSource(RetrofitInstance.api, filters) }
        ).flow.cachedIn(viewModelScope)
    }

    fun applySearchFilters(filters: Map<String, String?>) {
        _searchFilters.value = filters
    }

    fun clearFilters() {
        _searchFilters.value = emptyMap()
    }


}
