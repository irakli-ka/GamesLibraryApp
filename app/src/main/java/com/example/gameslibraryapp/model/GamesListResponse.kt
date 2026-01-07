package com.example.gameslibraryapp.model

import com.google.gson.annotations.SerializedName

data class GamesListResponse(

    @SerializedName("next")
    val next: String?,

    @SerializedName("previous")
    val previous: String?,

    @SerializedName("results")
    val results: List<Game>
)
