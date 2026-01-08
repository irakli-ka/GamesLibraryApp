import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.gameslibraryapp.BuildConfig
import com.example.gameslibraryapp.interfaces.RawgApi
import com.example.gameslibraryapp.model.Game
import retrofit2.HttpException
import java.io.IOException

class GamePagingSource(
    private val api: RawgApi,
    private val dates: String,
    private val ordering: String
) : PagingSource<Int, Game>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Game> {
        val page = params.key ?: 1

        return try {
            val response = api.getGames(
                apiKey = BuildConfig.API_KEY,
                dates = dates,
                ordering = ordering,
                page = page
            )

            val games = response.body()?.results ?: emptyList()

            LoadResult.Page(
                data = games,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (games.isEmpty()) null else page + 1
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Game>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}