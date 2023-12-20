package espressodev.gptmap.model.gemini

import kotlinx.serialization.Serializable

@Serializable
data class Content(
    val coordinates: Coordinates = Coordinates(),
    val city: String = "Istanbul",
    val district: String? = null,
    val country: String = "Turkey",
    val poeticDescription: String = "Place of the most beautiful sunsets",
    val normalDescription: String = ""
)