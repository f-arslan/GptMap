package com.espressodev.gptmap.core.gemini

import android.graphics.Bitmap
import com.espressodev.gptmap.core.model.Location
import kotlinx.coroutines.flow.Flow

interface GeminiDataSource {
    suspend fun getLocationInfo(textContent: String): Result<Location>
    fun getImageDescription(bitmap: Bitmap, text: String): Result<Flow<String>>
}
