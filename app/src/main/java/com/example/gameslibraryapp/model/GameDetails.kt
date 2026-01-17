package com.example.gameslibraryapp.model

import com.google.gson.annotations.SerializedName

data class Developer(val name: String)
data class Tag(val name: String)
data class StoreInfo(val store: Store)

data class GameDetails(
    val id: Int,
    val name: String,
    val rating: Double,
    @SerializedName("background_image")
    val backgroundImage: String?,
    val genres: List<Genre>?,
    val platforms: List<PlatformInfo>?,
    @SerializedName("description_raw")
    val descriptionRaw: String?,
    val website: String?,
    val released: String?,
    val developers: List<Developer>?,
    val stores: List<StoreInfo>?,
    val tags: List<Tag>?,

)