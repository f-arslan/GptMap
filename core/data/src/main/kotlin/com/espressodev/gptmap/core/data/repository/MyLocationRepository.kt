package com.espressodev.gptmap.core.data.repository

import kotlinx.coroutines.flow.Flow

interface MyLocationRepository {
    suspend fun getCurrentLocation(): Flow<Result<Pair<Double, Double>>>
}
