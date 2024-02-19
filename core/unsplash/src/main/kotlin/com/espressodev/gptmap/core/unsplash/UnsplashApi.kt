package com.espressodev.gptmap.core.unsplash

import com.espressodev.gptmap.core.model.unsplash.LocationImage
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface UnsplashApi {
    @GET("/photos")
    suspend fun getTwoPhotos(@Query("query") query: String): Response<List<LocationImage>>
}
