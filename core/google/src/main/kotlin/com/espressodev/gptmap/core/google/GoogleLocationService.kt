package com.espressodev.gptmap.core.google

import kotlinx.coroutines.flow.Flow

interface GoogleLocationService {
    suspend fun getCurrentLocation(): Flow<Result<Pair<Double, Double>>>
}
