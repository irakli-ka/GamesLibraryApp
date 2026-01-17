package com.example.gameslibraryapp.viewmodel

import GamePagingSource
import RetrofitInstance
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import com.example.gameslibraryapp.BuildConfig
import com.example.gameslibraryapp.model.Game
import com.example.gameslibraryapp.model.Genre
import com.example.gameslibraryapp.model.Store
import com.example.gameslibraryapp.repository.UserProfile
import com.example.gameslibraryapp.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

sealed class AuthState {
    object Unknown : AuthState()
    object LoggedIn : AuthState()
    object LoggedOut : AuthState()
}

class MainViewModel : ViewModel() {

    private val _carouselGames = MutableStateFlow<List<Game>>(emptyList())
    val carouselGames = _carouselGames.asStateFlow()
    private val userRepository = UserRepository()
    private val _userProfile = MutableLiveData<UserProfile?>()
    val userProfile: LiveData<UserProfile?> get() = _userProfile
    private val _genres = MutableLiveData<List<Genre>>()
    val genres: LiveData<List<Genre>> get() = _genres
    private val _authState = MutableStateFlow<AuthState>(AuthState.Unknown)
    val authState = _authState.asStateFlow()
    private val _stores = MutableLiveData<List<Store>>()
    val stores: LiveData<List<Store>> get() = _stores
    private val _libraryGameIds = MutableStateFlow<Set<Int>>(emptySet())
    val libraryGameIds = _libraryGameIds.asStateFlow()
    private val _libraryGames = MutableLiveData<List<Game>>()
    val libraryGames: LiveData<List<Game>> get() = _libraryGames

    private val excludedIds = _carouselGames.map { games ->
        games.map { it.id }.toSet()
    }
    private val _feedFilters = MutableStateFlow<Map<String, String?>>(
        mapOf("" to "")
    )
    private val _searchResultLibrary = MutableLiveData<List<Game>>()
    val searchResultLibrary: LiveData<List<Game>> = _searchResultLibrary

    private val _searchStatus = MutableLiveData<String>()
    val searchStatus: LiveData<String> = _searchStatus

    private val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        val user = firebaseAuth.currentUser
        if (user != null) {
            _authState.value = AuthState.LoggedIn
            fetchUserProfile()
            fetchUserLibrary()
            fetchLibraryGames()
        } else {
            _authState.value = AuthState.LoggedOut
            _userProfile.value = null
            _libraryGameIds.value = emptySet()
            _libraryGames.value = emptyList()
        }
    }

    init {
        FirebaseAuth.getInstance().addAuthStateListener(authStateListener)

        fetchGenres()
        fetchStores()
    }

    override fun onCleared() {
        super.onCleared()
        FirebaseAuth.getInstance().removeAuthStateListener(authStateListener)
    }

    private fun fetchUserProfile() {
        viewModelScope.launch {
            var profile = userRepository.getCurrentUserProfile()

            if (profile == null && _authState.value == AuthState.LoggedIn) {
                kotlinx.coroutines.delay(700)
                profile = userRepository.getCurrentUserProfile()
            }

            _userProfile.postValue(profile)
        }
    }

    private fun fetchGenres() {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getGenres(BuildConfig.API_KEY)
                if (response.isSuccessful && response.body() != null) {
                    _genres.postValue(response.body()!!.results)
                }
            } catch (e: Exception) {
            }
        }
    }

    private fun fetchStores() {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getStores(BuildConfig.API_KEY)
                if (response.isSuccessful && response.body() != null) {
                    _stores.postValue(response.body()!!.results)
                }
            } catch (e: Exception) {
            }
        }
    }


    val gamesFeed: Flow<PagingData<Game>> = _feedFilters.flatMapLatest { filters ->
        Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            pagingSourceFactory = { GamePagingSource(RetrofitInstance.api, filters) }
        ).flow
    }.cachedIn(viewModelScope)
        .combine(excludedIds) { pagingData, ids ->
            pagingData.filter { game -> !ids.contains(game.id) }
        }

    fun applyFilters(filters: Map<String, String?>) {
        _feedFilters.value = filters
    }

    fun setCarouselGames(games: List<Game>) {
        if (_carouselGames.value.isEmpty()) {
            _carouselGames.value = games
        }
    }

    fun saveGameToLibrary(game: Game) {
        if (_authState.value == AuthState.LoggedIn) {
            viewModelScope.launch {
                try {
                    if (_libraryGameIds.value.contains(game.id)) {
                        return@launch
                    }
                    userRepository.addGameToLibrary(game)
                } catch (e: Exception) {
                }
            }
        }
    }

    private fun fetchUserLibrary() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val dbRef = FirebaseDatabase.getInstance().getReference("user_library/$userId")

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val ids = snapshot.children.mapNotNull { it.key?.toIntOrNull() }.toSet()
                _libraryGameIds.value = ids

                fetchLibraryGames()
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    fun removeGameFromLibrary(gameId: Int) {
        if (_authState.value == AuthState.LoggedIn) {
            viewModelScope.launch {
                try {
                    userRepository.removeGameFromLibrary(gameId)
                } catch (e: Exception){ }
            }
        }
    }

    fun fetchLibraryGames() {
        viewModelScope.launch {
            _libraryGames.value = userRepository.getLibraryGames()
        }
    }
    fun hasCarouselData(): Boolean = _carouselGames.value.isNotEmpty()

    fun searchUserLibrary(username: String) {
        viewModelScope.launch {
            _searchStatus.value = "Searching for $username..."
            val uid = userRepository.getUidByUsername(username)

            if (uid != null) {
                val games = userRepository.getLibraryByUid(uid)
                _searchResultLibrary.value = games
                _searchStatus.value = if (games.isEmpty()) "$username's library is empty." else "Showing $username's library"
            } else {
                _searchResultLibrary.value = emptyList()
                _searchStatus.value = "User '$username' not found."
            }
        }
    }

    fun clearSearch() {
        _searchResultLibrary.value = emptyList()
        _searchStatus.value = ""
    }

}