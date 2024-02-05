package com.espressodev.gptmap.api.gemini.impl

import android.graphics.Bitmap
import com.espressodev.gptmap.api.gemini.GeminiService
import com.espressodev.gptmap.core.model.Exceptions.ResponseTextNotFoundException
import com.espressodev.gptmap.core.model.Location
import com.espressodev.gptmap.core.model.ext.toLocation
import com.espressodev.gptmap.core.model.PromptUtil.locationPreText
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GeminiServiceImpl(
    private val generativeModelForText: GenerativeModel,
    private val generativeModelForImage: GenerativeModel
) : GeminiService {
    override suspend fun getLocationInfo(textContent: String): Result<Location> =
        withContext(Dispatchers.IO) {
            runCatching {
                generativeModelForText.generateContent(locationPreText + textContent).text?.toLocation()
                    ?: throw ResponseTextNotFoundException()
            }
        }

    override suspend fun getImageDescription(image: Bitmap, text: String): Result<String> =
        withContext(Dispatchers.IO) {
            runCatching {
                val inputContent = content {
                    image(image)
                    text(text)
                }
                generativeModelForImage.generateContentStream(inputContent).collect { chunk ->
                    println(chunk)
                }
                ""
            }
        }
}
