package com.espressodev.gptmap.core.gemini_api.module

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.espressodev.gptmap.core.gemini_api.BuildConfig.PALM_API_KEY
import com.espressodev.gptmap.core.gemini_api.GeminiService
import com.espressodev.gptmap.core.gemini_api.impl.GeminiServiceImpl
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.BlockThreshold
import com.google.ai.client.generativeai.type.HarmCategory
import com.google.ai.client.generativeai.type.SafetySetting
import dagger.Provides
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GeminiModule {

    private val harassmentSafety = SafetySetting(HarmCategory.HARASSMENT, BlockThreshold.ONLY_HIGH)
    private val hateSpeechSafety = SafetySetting(HarmCategory.HATE_SPEECH, BlockThreshold.MEDIUM_AND_ABOVE)

    @Singleton
    @Provides
    fun provideGenerativeModel(): GenerativeModel = GenerativeModel(
        modelName = "gemini-pro",
        apiKey = PALM_API_KEY,
        safetySettings = listOf(harassmentSafety, hateSpeechSafety)
    )

    @Singleton
    @Provides
    fun bindGeminiService(generativeModel: GenerativeModel): GeminiService =
        GeminiServiceImpl(generativeModel)
}