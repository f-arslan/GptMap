package com.espressodev.gptmap.core.model.chatgpt

import kotlinx.serialization.Serializable

@Serializable
data class ChatgptRequest(
    val messages: List<Message>,
    val model: String = "gpt-3.5-turbo",
    val temperature: Double = 0.7
)
