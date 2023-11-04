package com.espressodev.gptmap.core.model

import com.google.firebase.firestore.DocumentId

data class User(
    @DocumentId val userId: String = "",
    val fullName: String = "",
    val email: String = "",
    val profilePictureUrl: String = "",
    val fcmToken: String = "",
    val isEmailVerified: Boolean = false,
    val provider: Provider = Provider.DEFAULT
)

enum class Provider {
    DEFAULT, GOOGLE, FACEBOOK, X
}
