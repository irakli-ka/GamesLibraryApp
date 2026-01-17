package com.example.gameslibraryapp.model

data class ScreenshotListResponse(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<ShortScreenshot>
)