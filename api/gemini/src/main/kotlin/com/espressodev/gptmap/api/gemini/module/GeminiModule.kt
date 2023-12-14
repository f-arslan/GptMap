package com.espressodev.gptmap.api.gemini.module

import com.espressodev.gptmap.api.gemini.GeminiService
import com.espressodev.gptmap.api.gemini.impl.GeminiServiceImpl
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.BlockThreshold
import com.google.ai.client.generativeai.type.HarmCategory
import com.google.ai.client.generativeai.type.SafetySetting
import dagger.Provides
import javax.inject.Singleton
import com.espressodev.gptmap.api.gemini.BuildConfig.PALM_API_KEY

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