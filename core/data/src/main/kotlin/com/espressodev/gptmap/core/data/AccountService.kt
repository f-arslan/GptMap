package com.espressodev.gptmap.core.data

import com.espressodev.gptmap.core.model.Response
import com.espressodev.gptmap.core.model.User
import com.google.firebase.auth.AuthResult


typealias SendEmailVerificationResponse = Response<Boolean>
typealias SignInResponse = Response<Boolean>
typealias ReloadUserResponse = Response<Boolean>
typealias SendPasswordResetEmailResponse = Response<Boolean>
typealias RevokeAccessResponse = Response<Boolean>
typealias UpdatePasswordResponse = Response<Boolean>

interface AccountService {
    val isEmailVerified: Boolean
    suspend fun firebaseSignUpWithEmailAndPassword(email: String, password: String, fullName: String): AuthResult

    suspend fun sendEmailVerification(): SendEmailVerificationResponse

    suspend fun firebaseSignInWithEmailAndPassword(email: String, password: String): SignInResponse

    suspend fun reloadFirebaseUser(): ReloadUserResponse

    suspend fun updatePassword(password: String): UpdatePasswordResponse

    suspend fun sendPasswordResetEmail(email: String): SendPasswordResetEmailResponse

    fun signOut()

    suspend fun revokeAccess(): RevokeAccessResponse
}