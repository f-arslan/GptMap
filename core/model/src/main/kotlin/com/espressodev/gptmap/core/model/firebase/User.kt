package com.espressodev.gptmap.core.model.firebase

import androidx.annotation.Keep
import androidx.compose.runtime.Stable
import com.espressodev.gptmap.core.model.realm.RealmUser
import com.google.firebase.firestore.DocumentId

@Stable
data class User @Keep constructor(
    @DocumentId val userId: String = "",
    val fullName: String = "",
    val email: String = "",
    val profilePictureUrl: String = "",
    val fcmToken: String = "",
    val provider: String = Provider.DEFAULT.name
)

fun User.toRealmUser(): RealmUser = RealmUser().apply {
    firebaseId = this@User.userId
    email = this@User.email
    profilePictureUrl = this@User.profilePictureUrl
    fcmToken = this@User.fcmToken
    provider = this@User.provider
}

enum class Provider {
    DEFAULT, GOOGLE
}
