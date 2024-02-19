package com.espressodev.gptmap.core.gemini.impl

import android.graphics.Bitmap
import com.espressodev.gptmap.core.gemini.GeminiDataSource
import com.espressodev.gptmap.core.model.Exceptions.ResponseTextNotFoundException
import com.espressodev.gptmap.core.model.Location
import com.espressodev.gptmap.core.model.PromptUtil.locationPreText
import com.espressodev.gptmap.core.model.ext.toLocation
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.withContext

class GeminiDataSourceImpl(
    private val generativeModelForText: GenerativeModel,
    private val generativeModelForImage: GenerativeModel
) : GeminiDataSource {
    override suspend fun getLocationInfo(textContent: String): Result<Location> =
        withContext(Dispatchers.IO) {
            runCatching {
                generativeModelForText.generateContent(locationPreText + textContent).text?.toLocation()
                    ?: throw ResponseTextNotFoundException()
            }
        }

    override fun getImageDescription(bitmap: Bitmap, text: String): Result<Flow<String>> =
        runCatching {
            val inputContent = content {
                image(bitmap)
                text(text)
            }
            generativeModelForImage.generateContentStream(inputContent)
                .map { it.text }
                .mapNotNull { it }
        }
}
