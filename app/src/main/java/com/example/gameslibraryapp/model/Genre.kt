package com.example.gameslibraryapp.model

import com.google.gson.annotations.SerializedName

data class Genre(
    @SerializedName("id")
    override val id: Int = 0,

    @SerializedName("name")
    override val name: String = ""
) : FilterableItem
