package com.example.gameslibraryapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gameslibraryapp.BuildConfig
import com.example.gameslibraryapp.model.Game
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    // Private MutableLiveData that can be modified within the ViewModel
    private val _games = MutableLiveData<List<Game>>()

    // Public LiveData that the Fragment can observe but not change
    val games: LiveData<List<Game>> get() = _games

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    // This function will be called from the Fragment to start the network request
    fun fetchGames() {
        // Use viewModelScope to launch a coroutine that is automatically cancelled when the ViewModel is cleared
        viewModelScope.launch {
            try {
                // Call the suspend function from our API interface
                val response = RetrofitInstance.api.getGames(
                    apiKey = BuildConfig.API_KEY, // Access the key from BuildConfig
                    dates = "", // Example date range
                    ordering = "" // Sort by rating descending
                )

                if (response.isSuccessful && response.body() != null) {
                    // Post the list of games to the LiveData object
                    _games.postValue(response.body()!!.results)
                } else {
                    // Handle API errors (e.g., 404, 500)
                    _errorMessage.postValue("API Error: ${response.code()}")
                }
            } catch (e: Exception) {
                // Handle network errors (e.g., no internet)
                _errorMessage.postValue("Network Error: ${e.message}")
            }
        }
    }
}