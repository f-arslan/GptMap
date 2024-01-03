package com.espressodev.gptmap.core.model.chatgpt

import kotlinx.serialization.Serializable

@Serializable
data class Coordinates(
    val latitude: Double = 41.0082,
    val longitude: Double = 28.9784
)
