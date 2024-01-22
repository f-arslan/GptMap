package com.espressodev.gptmap.core.data.impl

import com.espressodev.gptmap.core.data.AccountService
import com.espressodev.gptmap.core.data.FirestoreService
import com.espressodev.gptmap.core.model.Exceptions
import com.espressodev.gptmap.core.model.Exceptions.FirebaseUserIsNullException
import com.espressodev.gptmap.core.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.dataObjects
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import java.util.concurrent.ExecutionException
import javax.inject.Inject

class FirestoreServiceImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val accountService: AccountService,
) : FirestoreService {

    override fun saveUser(user: User) {
        userColRef.document(user.userId).set(user)
    }

    override suspend fun isUserInDatabase(email: String): Result<Boolean> =
        try {
            val querySnapshot = userColRef.whereEqualTo("email", email).get().await()
            Result.success(querySnapshot.size() > 0)
        } catch (e: FirebaseFirestoreException) {
            Result.failure(e)
        } catch (e: InterruptedException) {
            Result.failure(e)
        } catch (e: ExecutionException) {
            Result.failure(e)
        }

    override suspend fun getUser(): User =
        accountService.userId?.let { userId ->
            getUserDocRef(userId).get().await().toObject(User::class.java)
        } ?: throw FirebaseUserIsNullException()

    override fun getUserFlow(): Flow<User?> =
        accountService.userId?.let { userId ->
            getUserDocRef(userId).dataObjects<User>()
        } ?: throw FirebaseUserIsNullException()

    private val userColRef by lazy { firestore.collection(USERS) }
    private fun getUserDocRef(id: String) = userColRef.document(id)

    companion object {
        private const val USERS = "users"
    }
}
