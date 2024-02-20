package com.espressodev.gptmap.core.gemini.module

import com.espressodev.gptmap.core.gemini.BuildConfig.PALM_API_KEY
import com.espressodev.gptmap.core.gemini.GeminiRepository
import com.espressodev.gptmap.core.gemini.impl.GeminiDataSource
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.BlockThreshold
import com.google.ai.client.generativeai.type.HarmCategory
import com.google.ai.client.generativeai.type.SafetySetting
import com.google.ai.client.generativeai.type.generationConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GeminiModule {
    private val harassmentSafety = SafetySetting(HarmCategory.HARASSMENT, BlockThreshold.ONLY_HIGH)
    private val hateSpeechSafety =
        SafetySetting(HarmCategory.HATE_SPEECH, BlockThreshold.MEDIUM_AND_ABOVE)

    private val config = generationConfig {
        temperature = 0.8f
        topK = 16
        topP = 0.1f
        maxOutputTokens = 200
        stopSequences = listOf("red")
    }

    @Named(TEXT_MODEL)
    @Singleton
    @Provides
    fun provideGenerativeModelForText(): GenerativeModel = GenerativeModel(
        modelName = "gemini-pro",
        apiKey = PALM_API_KEY,
        safetySettings = listOf(harassmentSafety, hateSpeechSafety)
    )

    @Named(IMAGE_MODEL)
    @Singleton
    @Provides
    fun provideGenerativeModelForImage(): GenerativeModel = GenerativeModel(
        modelName = "gemini-pro-vision",
        apiKey = PALM_API_KEY,
        safetySettings = listOf(harassmentSafety, hateSpeechSafety),
        generationConfig = config
    )

    @Singleton
    @Provides
    fun bindGeminiService(
        @Named(TEXT_MODEL) generativeModelForText: GenerativeModel,
        @Named(IMAGE_MODEL) generativeModelForImage: GenerativeModel
    ): GeminiRepository =
        GeminiDataSource(
            generativeModelForText = generativeModelForText,
            generativeModelForImage = generativeModelForImage
        )

    private const val TEXT_MODEL = "text_model"
    private const val IMAGE_MODEL = "image_model"
}
