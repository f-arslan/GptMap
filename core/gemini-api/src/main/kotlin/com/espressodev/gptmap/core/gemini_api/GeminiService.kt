package com.espressodev.gptmap.core.gemini_api

import com.espressodev.gptmap.core.model.Location

interface GeminiService {
    suspend fun getLocationInfo(textContent: String): Result<Location>
}