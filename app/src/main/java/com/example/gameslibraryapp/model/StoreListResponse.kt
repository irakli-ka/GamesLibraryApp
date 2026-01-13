package com.example.gameslibraryapp.model

class StoreListResponse (
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<Store>
)