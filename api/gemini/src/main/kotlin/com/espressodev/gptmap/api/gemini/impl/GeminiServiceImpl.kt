package com.espressodev.gptmap.api.gemini.impl

import com.espressodev.gptmap.api.gemini.GeminiService
import com.espressodev.gptmap.core.model.Location
import com.espressodev.gptmap.core.model.ext.classTag
import com.espressodev.gptmap.core.model.ext.toLocation
import com.espressodev.gptmap.core.model.utils.PromptUtil.locationPreText
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GeminiServiceImpl(private val generativeModel: GenerativeModel) : GeminiService {
    override suspend fun getLocationInfo(textContent: String): Result<Location> =
        withContext(Dispatchers.IO) {
            try {
                generativeModel.generateContent(locationPreText + textContent).text?.let { responseText ->
                    Result.success(responseText.toLocation())
                } ?: throw Exception("${classTag()} responseTextNotFound")
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}