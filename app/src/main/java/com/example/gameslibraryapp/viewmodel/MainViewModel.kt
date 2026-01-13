import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import com.example.gameslibraryapp.model.Game
import com.example.gameslibraryapp.repository.UserProfile
import com.example.gameslibraryapp.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val _carouselGames = MutableStateFlow<List<Game>>(emptyList())
    val carouselGames = _carouselGames.asStateFlow()
    private val userRepository = UserRepository()
    private val _userProfile = MutableLiveData<UserProfile?>()
    val userProfile: LiveData<UserProfile?> get() = _userProfile

    private val excludedIds = _carouselGames.map { games ->
        games.map { it.id }.toSet()
    }

    init {
        fetchUserProfile()
    }


    private fun fetchUserProfile() {
        viewModelScope.launch {
            val profile = userRepository.getCurrentUserProfile()
            _userProfile.postValue(profile)
        }
    }

    val gamesFeed: Flow<PagingData<Game>> = Pager(
        config = PagingConfig(pageSize = 20, enablePlaceholders = false),
        pagingSourceFactory = { GamePagingSource(RetrofitInstance.api, "", "") }
    ).flow
        .cachedIn(viewModelScope)
        .combine(excludedIds) { pagingData, ids ->
            pagingData.filter { game -> !ids.contains(game.id) }
        }

    fun setCarouselGames(games: List<Game>) {
        if (_carouselGames.value.isEmpty()) {
            _carouselGames.value = games
        }
    }


    fun hasCarouselData(): Boolean = _carouselGames.value.isNotEmpty()


}