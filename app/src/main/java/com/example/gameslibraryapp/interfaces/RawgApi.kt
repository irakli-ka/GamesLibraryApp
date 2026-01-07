package com.example.gameslibraryapp.interfaces

import com.example.gameslibraryapp.model.GamesListResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query


interface RawgApi {

    @GET("games")
    suspend fun getGames(
        @Query("key") apiKey: String,
        @Query("dates") dates: String,
        @Query("ordering") ordering: String
    ): Response<GamesListResponse>
}