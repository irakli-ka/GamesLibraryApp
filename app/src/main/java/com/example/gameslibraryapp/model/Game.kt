package com.example.gameslibraryapp.model

import com.google.gson.annotations.SerializedName

data class Game(
    @SerializedName("id")
    val id: Int = 0,

    @SerializedName("name")
    val name: String = "",

    @SerializedName("background_image")
    val backgroundImage: String? = null,

    @SerializedName("platforms")
    val platforms: List<PlatformInfo> = emptyList(),

    @SerializedName("rating")
    val rating: Double = 0.0,

    @SerializedName("genres")
    val genres: List<Genre> = emptyList()
)