package com.espressodev.gptmap.api.gemini

import android.graphics.Bitmap
import com.espressodev.gptmap.core.model.Location
import kotlinx.coroutines.flow.Flow

interface GeminiService {
    suspend fun getLocationInfo(textContent: String): Result<Location>
    fun getImageDescription(bitmap: Bitmap, text: String): Flow<String>
}
