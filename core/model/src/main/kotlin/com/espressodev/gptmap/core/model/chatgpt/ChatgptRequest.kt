package com.espressodev.gptmap.core.model.chatgpt

import com.google.gson.annotations.SerializedName

data class ChatgptRequest(
    @SerializedName("messages")
    val messages: List<Message>,
    @SerializedName("model")
    val model: String = "gpt-3.5-turbo",
    @SerializedName("temperature")
    val temperature: Double = 0.7
)
