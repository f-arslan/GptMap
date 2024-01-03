package com.espressodev.gptmap.core.model

import com.espressodev.gptmap.core.model.chatgpt.Content
import com.espressodev.gptmap.core.model.chatgpt.Coordinates

data class Content(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val city: String = "",
    val district: String = "",
    val country: String = "",
    val poeticDescription: String = "",
    val normalDescription: String = ""
) {
    fun toChatgptContent(): Content = Content(
        coordinates = Coordinates(latitude, longitude),
        city = city,
        district = district,
        country = country,
        poeticDescription = poeticDescription,
        normalDescription = normalDescription
    )
}
