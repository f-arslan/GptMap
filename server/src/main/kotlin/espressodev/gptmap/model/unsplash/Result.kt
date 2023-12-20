package espressodev.gptmap.model.unsplash

import kotlinx.serialization.Serializable

@Serializable
data class Result(
    val blur_hash: String?,
    val color: String?,
    val created_at: String?,
    // val current_user_collections: List<Any>,
    val description: String?,
    val height: Int?,
    val id: String?,
    val liked_by_user: Boolean?,
    val likes: Int?,
    val links: Links?,
    val urls: Urls,
    val user: User,
    val width: Int?
)