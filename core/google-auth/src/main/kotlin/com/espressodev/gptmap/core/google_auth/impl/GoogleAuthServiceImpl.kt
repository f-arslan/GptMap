package com.espressodev.gptmap.core.google_auth.impl

import com.espressodev.gptmap.core.data.FirestoreService
import com.espressodev.gptmap.core.google_auth.GoogleAuthService
import com.espressodev.gptmap.core.google_auth.OneTapSignInUpResponse
import com.espressodev.gptmap.core.google_auth.SignInUpWithGoogleResponse
import com.espressodev.gptmap.core.model.User
import com.espressodev.gptmap.core.model.google.GoogleConstants.SIGN_IN_REQUEST
import com.espressodev.gptmap.core.model.google.GoogleConstants.SIGN_UP_REQUEST
import com.espressodev.gptmap.core.model.google.GoogleResponse
import com.espressodev.gptmap.core.mongodb.impl.MongoService.APP_ID
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.Credentials
import io.realm.kotlin.mongodb.GoogleAuthType
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class GoogleAuthServiceImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val oneTapClient: SignInClient,
    @Named(SIGN_IN_REQUEST)
    private val signInRequest: BeginSignInRequest,
    @Named(SIGN_UP_REQUEST)
    private val signUpRequest: BeginSignInRequest,
    private val firestoreService: FirestoreService,
) : GoogleAuthService {
    override suspend fun oneTapSignInWithGoogle(): OneTapSignInUpResponse {
        return try {
            val signInResult = oneTapClient.beginSignIn(signInRequest).await()
            GoogleResponse.Success(signInResult)
        } catch (e: Exception) {
            try {
                val signUpResult = oneTapClient.beginSignIn(signUpRequest).await()
                GoogleResponse.Success(signUpResult)
            } catch (e: Exception) {
                GoogleResponse.Failure(e)

            }
        }
    }

    override suspend fun oneTapSignUpWithGoogle(): OneTapSignInUpResponse {
        return try {
            val signUpResult = oneTapClient.beginSignIn(signUpRequest).await()
            GoogleResponse.Success(signUpResult)
        } catch (e: Exception) {
            GoogleResponse.Failure(e)
        }
    }

    override suspend fun firebaseSignInWithGoogle(
        googleCredential: AuthCredential,
        token: String?,
    ): SignInUpWithGoogleResponse {
        return try {
            val authResult = auth.signInWithCredential(googleCredential).await()
            val isNewUser = authResult.additionalUserInfo?.isNewUser ?: false
            if (isNewUser) {
                authResult.user?.let { addUserToFirestore(it) }
            }
            token?.let {
                App.create(APP_ID).login(Credentials.jwt(token)).also {
                    println(it)
                }
            }
            GoogleResponse.Success(true)
        } catch (e: Exception) {
            GoogleResponse.Failure(e)
        }
    }

    private suspend fun addUserToFirestore(firebaseUser: FirebaseUser) {
        firebaseUser.apply {
            val displayName = displayName ?: throw Exception("Display name is null")
            val email = email ?: throw Exception("Email is null")
            val photoUrl = photoUrl ?: throw Exception("Photo url is null")
            val user = User(
                userId = uid,
                fullName = displayName,
                email = email,
                profilePictureUrl = photoUrl.toString()
            )
            firestoreService.saveUser(user)
        }
    }
}