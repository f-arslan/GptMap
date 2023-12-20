package espressodev.gptmap.model

import kotlinx.serialization.Serializable

@Serializable
data class LocationImage(
    val imageUrl: String,
    val imageAuthor: String,
)
