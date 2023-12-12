package com.espressodev.gptmap.core.model.unsplash

data class UnsplashResponse(
    val results: List<Result>,
    val total: Int,
    val total_pages: Int
)