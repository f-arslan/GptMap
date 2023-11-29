package com.espressodev.gptmap.core.data.impl

import com.espressodev.gptmap.core.data.FirestoreService
import com.espressodev.gptmap.core.model.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FirestoreServiceImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
) : FirestoreService {

    override suspend fun saveUser(user: User) {
        userColRef.document(user.userId).set(user)
    }

    override suspend fun isUserInDatabase(userId: String): Result<Boolean> =
        try {
            val user = getUserDocRef(userId).get().await()
            Result.success(user.exists())
        } catch (e: Exception) {
            Result.failure(e)
        }

    override suspend fun getUser(userId: String): Result<User> = withContext(Dispatchers.IO) {
        try {
            val user = getUserDocRef(userId).get().await().toObject(User::class.java)
                ?: throw Exception("User is null")
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateUserEmailVerification(
        userId: String,
        isEmailVerified: Boolean
    ): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            getUserDocRef(userId).update(USER_IS_EMAIL_VERIFIED, isEmailVerified).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    private val userColRef by lazy { firestore.collection(USERS) }
    private fun getUserDocRef(id: String) = userColRef.document(id)


    companion object {
        private const val USERS = "users"

        private const val USER_PROFILE_PICTURE_URL = "profilePictureUrl"
        private const val USER_IS_EMAIL_VERIFIED = "isEmailVerified"
    }
}