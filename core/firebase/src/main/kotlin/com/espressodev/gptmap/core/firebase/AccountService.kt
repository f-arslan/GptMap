package com.espressodev.gptmap.core.firebase

import com.espressodev.gptmap.core.model.Response
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser

typealias SendEmailVerificationResponse = Response<Boolean>
typealias ReloadUserResponse = Response<Boolean>
typealias SendPasswordResetEmailResponse = Response<Boolean>
typealias UpdatePasswordResponse = Response<Boolean>

interface AccountService {
    val userId: String?
    val isEmailVerified: Boolean
    val email: String?
    val firebaseUser: FirebaseUser?
    suspend fun firebaseSignUpWithEmailAndPassword(email: String, password: String): AuthResult
    suspend fun sendEmailVerification(): SendEmailVerificationResponse
    suspend fun firebaseSignInWithEmailAndPasswordAndReturnResult(email: String, password: String): AuthResult
    suspend fun firebaseSignInWithEmailAndPassword(email: String, password: String)
    suspend fun reloadFirebaseUser(): ReloadUserResponse
    suspend fun updatePassword(password: String): UpdatePasswordResponse
    suspend fun sendPasswordResetEmail(email: String): SendPasswordResetEmailResponse
    fun signOut()
    suspend fun revokeAccess(): Result<Unit>
}
