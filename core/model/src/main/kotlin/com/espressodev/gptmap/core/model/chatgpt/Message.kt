package com.espressodev.gptmap.core.model.chatgpt

import kotlinx.serialization.Serializable

@Serializable
data class Message(
    val content: String,
    val role: String = "user"
)
