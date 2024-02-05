package com.espressodev.gptmap.api.gemini.module

import com.espressodev.gptmap.api.gemini.GeminiService
import com.espressodev.gptmap.api.gemini.impl.GeminiServiceImpl
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.BlockThreshold
import com.google.ai.client.generativeai.type.HarmCategory
import com.google.ai.client.generativeai.type.SafetySetting
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.Module
import dagger.Provides
import javax.inject.Singleton
import com.espressodev.gptmap.api.gemini.BuildConfig.PALM_API_KEY
import com.google.ai.client.generativeai.type.generationConfig
import javax.inject.Named

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
        modelName = "gemini-pro",
        apiKey = PALM_API_KEY,
        safetySettings = listOf(harassmentSafety, hateSpeechSafety),
        generationConfig = config
    )

    @Singleton
    @Provides
    fun bindGeminiService(
        @Named(TEXT_MODEL) generativeModelForText: GenerativeModel,
        @Named(IMAGE_MODEL) generativeModelForImage: GenerativeModel
    ): GeminiService =
        GeminiServiceImpl(
            generativeModelForText = generativeModelForText,
            generativeModelForImage = generativeModelForImage
        )


    const val TEXT_MODEL = "text_model"
    const val IMAGE_MODEL = "image_model"
}
