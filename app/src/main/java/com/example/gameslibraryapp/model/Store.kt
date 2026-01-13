package com.example.gameslibraryapp.model

import FilterableItem
import com.google.gson.annotations.SerializedName

data class Store(
    @SerializedName("id")
    override val id: Int,

    @SerializedName("name")
    override val name: String

) : FilterableItem
