package com.espressodev.gptmap.core.domain

import com.espressodev.gptmap.core.google.GoogleLocationService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetCurrentLocationUseCase @Inject constructor(
    private val googleLocationService: GoogleLocationService,
    private val ioDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke() = withContext(ioDispatcher) {
        googleLocationService.getCurrentLocation()
    }
}
