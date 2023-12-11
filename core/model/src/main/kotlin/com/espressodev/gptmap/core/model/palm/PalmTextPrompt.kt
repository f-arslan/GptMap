package com.espressodev.gptmap.core.model.palm

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class PalmTextPrompt(
    val prompt: PalmText,
    @SerializedName("safety_settings")
    val safetySettings: List<SafetySetting> = SafetyCategory.entries.map { SafetySetting(it.name) },
    @SerializedName("candidate_count")
    val candidateCount: Int = 1,
    @SerializedName("max_output_tokens")
    val maxOutputTokens: Int = 1024,
    val temperature: Double = 0.4,
)
