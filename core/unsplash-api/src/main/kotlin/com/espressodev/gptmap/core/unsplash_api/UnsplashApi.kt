package com.espressodev.gptmap.core.unsplash_api

import com.espressodev.gptmap.core.model.unsplash.UnsplashResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface UnsplashApi {

    @GET("/search/photos?page=1&per_page=2&orientation=squarish")
    suspend fun getTwoPhotos(
        @Query("query") query: String
    ): Response<UnsplashResponse>

}