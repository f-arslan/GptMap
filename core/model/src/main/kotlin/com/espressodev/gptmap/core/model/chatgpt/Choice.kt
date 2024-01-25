package com.espressodev.gptmap.core.model.chatgpt

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class Choice(
    @SerializedName("finish_reason")
    val finishReason: String,
    val index: Int,
    val message: Message
)
