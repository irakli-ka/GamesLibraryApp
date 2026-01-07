package com.example.gameslibraryapp.model

import com.google.gson.annotations.SerializedName


data class PlatformInfo(
    @SerializedName("platform")
    val platform: Platform
)

data class Platform(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,
)