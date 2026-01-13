import com.example.gameslibraryapp.model.Genre

data class GenresListResponse(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<Genre>
)