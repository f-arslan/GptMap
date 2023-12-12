package com.espressodev.gptmap.core.palm

import com.espressodev.gptmap.core.model.Location


interface PalmService {
    suspend fun getLocationInfo(textContent: String): Result<Location>
}