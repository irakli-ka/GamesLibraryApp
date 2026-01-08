import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.gameslibraryapp.model.Game
import kotlinx.coroutines.flow.Flow

class MainViewModel : ViewModel() {

    // A Pager is the entry point for Paging 3. It connects the PagingSource (how to get data)
    // with a PagingConfig (how to load the data).
    val gamesFeed: Flow<PagingData<Game>> = Pager(
        // PagingConfig defines how to load data from the PagingSource.
        config = PagingConfig(
            pageSize = 20, // The number of items to load per page from the API.
            enablePlaceholders = false
        ),
        // The PagingSource factory creates an instance of our GamePagingSource,
        // which knows how to fetch pages from the RAWG API.
        pagingSourceFactory = {
            GamePagingSource(
                api = RetrofitInstance.api,
                dates = "", // Example filter
                ordering = "" // Example sorting
            )
        }
    ).flow
        // cachedIn() caches the data in the viewModelScope. This makes the data stream
        // survive configuration changes (like screen rotation) and allows it to be shared
        // between different UI components if needed.
        .cachedIn(viewModelScope)

}