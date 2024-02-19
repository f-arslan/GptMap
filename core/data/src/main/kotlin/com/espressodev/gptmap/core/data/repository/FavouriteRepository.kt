package com.espressodev.gptmap.core.data.repository

import com.espressodev.gptmap.core.model.Location

interface FavouriteRepository {
    suspend fun saveImageForLocation(location: Location): Result<Unit>
}