package com.espressodev.gptmap.core.model.chatgpt

import kotlinx.serialization.Serializable


@Serializable
data class Content(
    val coordinates: Coordinates,
    val city: String,
    val country: String,
    val description: String
)


