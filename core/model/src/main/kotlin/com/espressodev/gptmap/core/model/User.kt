package com.espressodev.gptmap.core.model

import androidx.compose.runtime.Stable
import com.google.firebase.firestore.DocumentId

@Stable
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
