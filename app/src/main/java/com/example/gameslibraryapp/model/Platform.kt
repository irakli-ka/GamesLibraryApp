package com.example.gameslibraryapp.model

import com.google.gson.annotations.SerializedName


data class PlatformInfo(
    @SerializedName("platform")
    val platform: Platform = Platform()
)

data class Platform(
    @SerializedName("id")
    val id: Int = 0,

    @SerializedName("name")
    val name: String = "",
)