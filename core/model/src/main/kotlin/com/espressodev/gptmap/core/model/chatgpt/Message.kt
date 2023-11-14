package com.espressodev.gptmap.core.model.chatgpt

import com.google.gson.annotations.SerializedName

data class Message(
    @SerializedName("content")
    val content: String,
    @SerializedName("role")
    val role: String
)