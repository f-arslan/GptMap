package com.espressodev.gptmap.core.model.chatgpt

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class Usage(
    @SerializedName("completion_tokens")
    val completionTokens: Int,
    @SerializedName("prompt_tokens")
    val promptTokens: Int,
    @SerializedName("total_tokens")
    val totalTokens: Int
)
