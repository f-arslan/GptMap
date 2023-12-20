package espressodev.gptmap.model.unsplash

import kotlinx.serialization.Serializable

@Serializable
data class Links(
    val download: String,
    val html: String,
    val self: String
)