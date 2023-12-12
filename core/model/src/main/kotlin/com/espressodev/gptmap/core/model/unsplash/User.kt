package com.espressodev.gptmap.core.model.unsplash

data class User(
    val first_name: String,
    val id: String,
    val instagram_username: String?,
    val last_name: String,
    val links: LinksX?,
    val name: String,
    val portfolio_url: String?,
    val profile_image: ProfileImage?,
    val twitter_username: String?,
    val username: String?
)