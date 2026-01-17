import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gameslibraryapp.BuildConfig
import com.example.gameslibraryapp.model.GameDetails
import com.example.gameslibraryapp.model.ShortScreenshot
import kotlinx.coroutines.launch

class GameDetailsViewModel : ViewModel() {

    private val _gameDetails = MutableLiveData<GameDetails?>()
    val gameDetails: LiveData<GameDetails?> get() = _gameDetails

    private val _screenshots = MutableLiveData<List<ShortScreenshot>>()
    val screenshots: LiveData<List<ShortScreenshot>> get() = _screenshots

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun fetchGameDetails(gameId: Int) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val detailsResponse =
                    RetrofitInstance.api.getGameDetails(gameId, BuildConfig.API_KEY)
                if (detailsResponse.isSuccessful) {
                    _gameDetails.postValue(detailsResponse.body())
                } else {
                    _gameDetails.postValue(null)
                }

                val screenshotsResponse =
                    RetrofitInstance.api.getGameScreenshots(gameId, BuildConfig.API_KEY)
                if (screenshotsResponse.isSuccessful) {
                    _screenshots.postValue(screenshotsResponse.body()?.results ?: emptyList())
                }

            } catch (e: Exception) {
                _gameDetails.postValue(null)
                _screenshots.postValue(emptyList())
            } finally {
                _isLoading.postValue(false)
            }
        }
    }
}