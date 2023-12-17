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
        userColRef.document(user.userId).set(user).await()
    }

    override suspend fun isUserInDatabase(email: String): Result<Boolean> =
        try {
            val querySnapshot = userColRef.whereEqualTo("email", email).get().await()
            Result.success(querySnapshot.size() > 0)
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


    private val userColRef by lazy { firestore.collection(USERS) }
    private fun getUserDocRef(id: String) = userColRef.document(id)


    companion object {
        private const val USERS = "users"
    }
}