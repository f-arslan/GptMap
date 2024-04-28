package com.espressodev.gptmap.core.data.repository.impl

import com.espressodev.gptmap.core.data.repository.MyLocationRepository
import com.espressodev.gptmap.core.google.GoogleLocationService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MyLocationRepositoryImpl @Inject constructor(
    private val googleLocationService: GoogleLocationService
): MyLocationRepository {
    override suspend fun getCurrentLocation(): Flow<Result<Pair<Double, Double>>> =
        googleLocationService.getCurrentLocation()
}
