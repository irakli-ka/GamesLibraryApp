import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import com.example.gameslibraryapp.model.Game
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class MainViewModel : ViewModel() {

    // 1. Store the actual games list, not just IDs
    private val _carouselGames = MutableStateFlow<List<Game>>(emptyList())
    val carouselGames = _carouselGames.asStateFlow()

    // We derive the IDs automatically from the list above
    private val excludedIds = _carouselGames.map { games ->
        games.map { it.id }.toSet()
    }

    val gamesFeed: Flow<PagingData<Game>> = Pager(
        config = PagingConfig(pageSize = 20, enablePlaceholders = false),
        pagingSourceFactory = { GamePagingSource(RetrofitInstance.api, "", "") }
    ).flow
        .cachedIn(viewModelScope)
        .combine(excludedIds) { pagingData, ids ->
            // Filter out any game that is currently in the carousel
            pagingData.filter { game -> !ids.contains(game.id) }
        }

    fun setCarouselGames(games: List<Game>) {
        // Only update if we don't have games yet (prevents overwriting on navigation)
        if (_carouselGames.value.isEmpty()) {
            _carouselGames.value = games
        }
    }

    // Helper to check if we already have data
    fun hasCarouselData(): Boolean = _carouselGames.value.isNotEmpty()
}