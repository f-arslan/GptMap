package com.espressodev.gptmap.core.model.chatgpt

import kotlinx.serialization.Serializable


@Serializable
data class Content(
    val coordinates: Coordinates = Coordinates(),
    val city: String = "Istanbul",
    val district: String? = null,
    val country: String = "Turkey",
    val poeticDescription: String = "Place of the most beautiful sunsets",
    val normalDescription: String = ""
) {
    fun toDistrictAndCountry() = district?.let {
        "$district, $country"
    } ?: country

    fun toPoeticDescWithDecor() = "\"$poeticDescription\""
}


