package com.example.gameslibraryapp.model

import com.google.gson.annotations.SerializedName

data class Genre(
    @SerializedName("id")
    override val id: Int,

    @SerializedName("name")
    override val name: String
) : FilterableItem
