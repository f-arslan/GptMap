package com.espressodev.gptmap.api.palm

import com.espressodev.gptmap.core.model.Location

interface PalmService {
    suspend fun getLocationInfo(textContent: String): Result<Location>
}
