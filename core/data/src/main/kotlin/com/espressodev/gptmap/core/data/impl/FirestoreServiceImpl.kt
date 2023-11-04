package com.espressodev.gptmap.core.data.impl

import com.espressodev.gptmap.core.data.AccountService
import com.espressodev.gptmap.core.data.FirestoreService
import com.espressodev.gptmap.core.model.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.dataObjects
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirestoreServiceImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val accountService: AccountService
) : FirestoreService {

    override val user: Flow<User?>
        get() = getUserDocRef(accountService.currentUserId).dataObjects<User>()

    override suspend fun saveUser(user: User) {
        userColRef.document(user.userId).set(user).await()
    }

    override suspend fun updateUserProfilePictureUrl(userId: String, profilePictureUrl: String) {
        getUserDocRef(userId).update(USER_PROFILE_PICTURE_URL, profilePictureUrl).await()
    }

    override suspend fun updateUserEmailVerification(userId: String) {
        getUserDocRef(userId).update(USER_IS_EMAIL_VERIFIED, true).await()
    }


    private val userColRef by lazy { firestore.collection(USERS) }
    private fun getUserDocRef(id: String) = userColRef.document(id)


    companion object {
        private const val TAG = "FirestoreServiceImpl"
        private const val USERS = "users"

        private const val USER_PROFILE_PICTURE_URL = "profilePictureUrl"
        private const val USER_IS_EMAIL_VERIFIED = "isEmailVerified"
    }
}