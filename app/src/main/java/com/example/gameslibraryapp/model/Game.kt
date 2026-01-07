package com.example.gameslibraryapp.model

import com.google.gson.annotations.SerializedName

data class Game(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("background_image")
    val backgroundImage: String?,

    @SerializedName("platforms")
    val platforms: List<PlatformInfo>,

    @SerializedName("rating")
    val rating: Double?,

    @SerializedName("genres")
    val genres: List<Genre>,
)