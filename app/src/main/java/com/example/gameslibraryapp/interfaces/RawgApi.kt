package com.example.gameslibraryapp.interfaces

import GenresListResponse
import com.example.gameslibraryapp.model.GamesListResponse
import com.example.gameslibraryapp.model.StoreListResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.QueryMap


interface RawgApi {
    @GET("games")
    suspend fun getGames(
        @Query("key")apiKey: String,
        @Query("page") page: Int,
        @QueryMap filters: Map<String, String?>
    ): Response<GamesListResponse>

    @GET("genres")
    suspend fun getGenres(
        @Query("key") apiKey: String,
    ): Response<GenresListResponse>

    @GET("stores")
    suspend fun getStores(
        @Query("key") apiKey: String,
    ): Response<StoreListResponse>

}