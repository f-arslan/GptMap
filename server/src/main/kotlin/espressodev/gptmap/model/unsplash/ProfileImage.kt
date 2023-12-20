package espressodev.gptmap.model.unsplash

import kotlinx.serialization.Serializable

@Serializable
data class ProfileImage(
    val large: String,
    val medium: String,
    val small: String
)