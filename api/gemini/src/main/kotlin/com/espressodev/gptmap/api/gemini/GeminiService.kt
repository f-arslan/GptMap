package com.espressodev.gptmap.api.gemini

import android.graphics.Bitmap
import com.espressodev.gptmap.core.model.Location

interface GeminiService {
    suspend fun getLocationInfo(textContent: String): Result<Location>
    suspend fun getImageDescription(image: Bitmap, text: String): Result<String>
}
