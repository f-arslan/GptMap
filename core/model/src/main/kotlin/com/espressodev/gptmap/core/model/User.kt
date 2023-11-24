package com.espressodev.gptmap.core.model

import androidx.compose.runtime.Stable
import com.espressodev.gptmap.core.model.realm.RealmUser
import com.google.firebase.firestore.DocumentId

@Stable
data class User(
    @DocumentId val userId: String = "",
    val fullName: String = "",
    val email: String = "",
    val profilePictureUrl: String = "",
    val fcmToken: String = "",
    val isEmailVerified: Boolean = false,
    val provider: String = Provider.DEFAULT.name
) {
    fun toRealmUser(): RealmUser = RealmUser().apply {
        firebaseId = userId
        email = this@User.email
        profilePictureUrl = this@User.profilePictureUrl
        fcmToken = this@User.fcmToken
        isEmailVerified = this@User.isEmailVerified
        provider = this@User.provider
    }
}

enum class Provider {
    DEFAULT, GOOGLE, FACEBOOK, X
}
