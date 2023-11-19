package com.espressodev.gptmap.core.model.chatgpt

import kotlinx.serialization.Serializable


@Serializable
data class Content(
    val coordinates: Coordinates,
    val city: String,
    val district: String? = null,
    val country: String,
    val poeticDescription: String,
    val normalDescription: String
) {
    fun toDistrictAndCountry() = district?.let {
        "$district, $country"
    } ?: country

    fun toPoeticDescWithDecor() = "\"$poeticDescription\""
}


