package com.espressodev.gptmap.core.gemini

import android.graphics.Bitmap
import com.espressodev.gptmap.core.model.Location
import kotlinx.coroutines.flow.Flow

interface GeminiRepository {
    suspend fun getLocationInfo(textContent: String): Result<Pair<Location, Int>>
    suspend fun getImageDescription(bitmap: Bitmap, text: String): Result<Flow<Pair<String, Int>>>
}
