package com.espressodev.gptmap.core.domain

import com.espressodev.gptmap.core.google.GoogleLocationService
import com.espressodev.gptmap.core.model.di.Dispatcher
import com.espressodev.gptmap.core.model.di.GmDispatchers.IO
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetCurrentLocationUseCase @Inject constructor(
    private val googleLocationService: GoogleLocationService,
    @Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke() = withContext(ioDispatcher) {
        googleLocationService.getCurrentLocation()
    }
}
