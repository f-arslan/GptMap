package com.espressodev.gptmap.api.gemini

import com.espressodev.gptmap.core.model.Location

interface GeminiService {
    suspend fun getLocationInfo(textContent: String): Result<Location>
}