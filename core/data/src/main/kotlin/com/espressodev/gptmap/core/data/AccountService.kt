package com.espressodev.gptmap.core.data

import com.espressodev.gptmap.core.model.Response
import com.espressodev.gptmap.core.model.User
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser


typealias SendEmailVerificationResponse = Response<Boolean>
typealias ReloadUserResponse = Response<Boolean>
typealias SendPasswordResetEmailResponse = Response<Boolean>
typealias RevokeAccessResponse = Response<Boolean>
typealias UpdatePasswordResponse = Response<Boolean>

interface AccountService {

    val currentUser: FirebaseUser?

    suspend fun firebaseSignUpWithEmailAndPassword(email: String, password: String, fullName: String): AuthResult

    suspend fun sendEmailVerification(): SendEmailVerificationResponse

    suspend fun firebaseSignInWithEmailAndPassword(email: String, password: String): AuthResult

    suspend fun reloadFirebaseUser(): ReloadUserResponse

    suspend fun updatePassword(password: String): UpdatePasswordResponse

    suspend fun sendPasswordResetEmail(email: String): SendPasswordResetEmailResponse

    fun signOut()

    suspend fun revokeAccess(): RevokeAccessResponse
}