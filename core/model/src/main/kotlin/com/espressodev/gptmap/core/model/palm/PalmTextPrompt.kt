package com.espressodev.gptmap.core.model.palm

import kotlinx.serialization.Serializable

@Serializable
data class PalmTextPrompt(
    val prompt: PalmText,
    val safety_settings: List<SafetySetting> = SafetyCategory.values().map { SafetySetting(it.name) },
    val candidate_count: Int = 1,
    val max_output_tokens: Int = 1024,
    val temperature: Double = 0.4,
)
