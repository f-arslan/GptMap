package com.espressodev.gptmap.core.firebase.impl

import com.espressodev.gptmap.core.firebase.AccountService
import com.espressodev.gptmap.core.firebase.ReloadUserResponse
import com.espressodev.gptmap.core.firebase.SendEmailVerificationResponse
import com.espressodev.gptmap.core.firebase.SendPasswordResetEmailResponse
import com.espressodev.gptmap.core.firebase.UpdatePasswordResponse
import com.espressodev.gptmap.core.model.Response
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AccountServiceImpl @Inject constructor(
    private val auth: FirebaseAuth,
) : AccountService {
    override val userId: String?
        get() = auth.currentUser?.uid

    override val isEmailVerified: Boolean
        get() = auth.currentUser?.isEmailVerified == true

    override val email: String?
        get() = auth.currentUser?.email

    override val firebaseUser: FirebaseUser?
        get() = auth.currentUser

    override suspend fun firebaseSignUpWithEmailAndPassword(
        email: String,
        password: String,
    ): AuthResult {
        return auth.createUserWithEmailAndPassword(email, password).await()
    }

    override suspend fun sendEmailVerification(): SendEmailVerificationResponse {
        return try {
            auth.currentUser?.sendEmailVerification()?.await()
            Response.Success(data = true)
        } catch (e: Exception) {
            Response.Failure(e)
        }
    }

    override suspend fun firebaseSignInWithEmailAndPasswordAndReturnResult(
        email: String,
        password: String,
    ): AuthResult = auth.signInWithEmailAndPassword(email, password).await()

    override suspend fun firebaseSignInWithEmailAndPassword(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).await()
    }

    override suspend fun reloadFirebaseUser(): ReloadUserResponse {
        return try {
            auth.currentUser?.reload()?.await()
            Response.Success(data = true)
        } catch (e: Exception) {
            Response.Failure(e)
        }
    }

    override suspend fun sendPasswordResetEmail(email: String): SendPasswordResetEmailResponse {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Response.Success(data = true)
        } catch (e: Exception) {
            Response.Failure(e)
        }
    }

    override suspend fun updatePassword(password: String): UpdatePasswordResponse {
        return try {
            auth.currentUser?.updatePassword(password)?.await()
            Response.Success(data = true)
        } catch (e: Exception) {
            Response.Failure(e)
        }
    }

    override fun signOut() = auth.signOut()

    override suspend fun revokeAccess(): Result<Unit> = runCatching {
        auth.currentUser?.delete()?.await()
    }
}
