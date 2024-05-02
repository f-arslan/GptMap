package com.espressodev.gptmap.core.gemini.impl

import android.graphics.Bitmap
import com.espressodev.gptmap.core.gemini.GeminiRepository
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

class GeminiDataSource(
    private val generativeModelForText: GenerativeModel,
    private val generativeModelForImage: GenerativeModel,
) : GeminiRepository {
    override suspend fun getLocationInfo(textContent: String): Result<Pair<Location, Int>> =
        withContext(Dispatchers.IO) {
            runCatching {
                val prompt = locationPreText + textContent
                val tokenCount =
                    generativeModelForText.countTokens(prompt).totalTokens
                val location = generativeModelForText.generateContent(prompt).text?.toLocation()
                    ?: throw ResponseTextNotFoundException()
                location to tokenCount
            }
        }

    override suspend fun getImageDescription(
        bitmap: Bitmap,
        text: String,
    ): Result<Flow<Pair<String, Int>>> =
        runCatching {
            val inputContent = content {
                image(bitmap)
                text(text)
            }
            val tokenCount =
                generativeModelForImage.countTokens(inputContent).totalTokens
            generativeModelForImage.generateContentStream(inputContent)
                .mapNotNull { it.text }
                .map { it to tokenCount }
        }
}
