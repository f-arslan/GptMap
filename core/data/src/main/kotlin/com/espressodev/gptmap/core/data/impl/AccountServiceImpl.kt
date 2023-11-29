package com.espressodev.gptmap.core.data.impl

import com.espressodev.gptmap.core.data.AccountService
import com.espressodev.gptmap.core.data.FirestoreService
import com.espressodev.gptmap.core.data.ReloadUserResponse
import com.espressodev.gptmap.core.data.RevokeAccessResponse
import com.espressodev.gptmap.core.data.SendEmailVerificationResponse
import com.espressodev.gptmap.core.data.SendPasswordResetEmailResponse
import com.espressodev.gptmap.core.data.SignInResponse
import com.espressodev.gptmap.core.data.UpdatePasswordResponse
import com.espressodev.gptmap.core.model.Response
import com.espressodev.gptmap.core.model.User
import com.espressodev.gptmap.core.mongodb.RealmAccountService
import com.espressodev.gptmap.core.mongodb.RealmSyncService
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AccountServiceImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestoreService: FirestoreService,
    private val realmSyncService: RealmSyncService,
    private val realmAccountService: RealmAccountService
) : AccountService {

    override val isEmailVerified: Boolean
        get() = auth.currentUser?.isEmailVerified ?: false


    override suspend fun firebaseSignUpWithEmailAndPassword(
        email: String,
        password: String,
        fullName: String
    ): AuthResult {
        return auth.createUserWithEmailAndPassword(email, password).await()
    }

    override suspend fun sendEmailVerification(): SendEmailVerificationResponse {
        return try {
            auth.currentUser?.sendEmailVerification()?.await()
            Response.Success(true)
        } catch (e: Exception) {
            Response.Failure(e)
        }
    }

    override suspend fun firebaseSignInWithEmailAndPassword(
        email: String,
        password: String,
    ): SignInResponse {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val isEmailVerified = authResult.user?.isEmailVerified ?: false

            loginToRealm(authResult)

            if (isEmailVerified) {
                authResult.user?.uid?.let {
                    updateUserIfUserEmailVerificationIsFalse(it)
                }
                    ?: throw UserIdIsNullException()
            }

            Response.Success(true)
        } catch (e: Exception) {
            Response.Failure(e)
        }
    }

    private suspend fun loginToRealm(authResult: AuthResult) {
        authResult.user?.getIdToken(true)?.await()?.token?.let {
            realmAccountService.loginWithEmail(it).onFailure { throwable ->
                throw Exception(throwable)
            }
        }
    }

    private suspend fun updateUserIfUserEmailVerificationIsFalse(userId: String) {
        firestoreService.getUser(userId).onSuccess { user ->
            if (!user.isEmailVerified) {
                firestoreService.updateUserEmailVerification(userId, true).onFailure {
                    throw FailedToUpdateUserEmailVerificationException()
                }
                realmSyncService.addUser(user.copy(isEmailVerified = true).toRealmUser())
            }
        }.onFailure {
            throw FailedToGetUserException()
        }
    }

    private suspend fun saveUserToDatabaseIfUserNotExist(user: User) {
        firestoreService.isUserInDatabase(user.userId).onSuccess { isUserInDb ->
            if (!isUserInDb)
                firestoreService.saveUser(user)
        }
    }

    override suspend fun reloadFirebaseUser(): ReloadUserResponse {
        return try {
            auth.currentUser?.reload()?.await()
            Response.Success(true)
        } catch (e: Exception) {
            Response.Failure(e)
        }
    }

    override suspend fun sendPasswordResetEmail(email: String): SendPasswordResetEmailResponse {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Response.Success(true)
        } catch (e: Exception) {
            Response.Failure(e)
        }
    }

    override suspend fun updatePassword(password: String): UpdatePasswordResponse {
        return try {
            auth.currentUser?.updatePassword(password)?.await()
            Response.Success(true)
        } catch (e: Exception) {
            Response.Failure(e)
        }
    }

    override fun signOut() = auth.signOut()

    override suspend fun revokeAccess(): RevokeAccessResponse {
        return try {
            auth.currentUser?.delete()?.await()
            Response.Success(true)
        } catch (e: Exception) {
            Response.Failure(e)
        }
    }
}

class FailedToGetUserException : Exception("Failed to get user")
class FailedToUpdateUserEmailVerificationException :
    Exception("Failed to update user email verification")

class UserIdIsNullException : Exception("User id is null")
