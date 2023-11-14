package com.espressodev.gptmap.core.model.chatgpt

import com.google.gson.annotations.SerializedName

data class ChatgptRequest(
    @SerializedName("messages")
    val messages: List<Message>,
    @SerializedName("model")
    val model: String,
    @SerializedName("temperature")
    val temperature: Double
)