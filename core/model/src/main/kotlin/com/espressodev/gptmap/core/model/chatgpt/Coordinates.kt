package com.espressodev.gptmap.core.model.chatgpt

import kotlinx.serialization.Serializable

@Serializable
data class Coordinates(
    val latitude: Double,
    val longitude: Double
)