package com.espressodev.gptmap.core.google.impl

import com.espressodev.gptmap.core.google.GoogleProfileService
import com.espressodev.gptmap.core.google.RevokeAccessResponse
import com.espressodev.gptmap.core.google.SignOutResponse
import com.espressodev.gptmap.core.model.google.GoogleResponse
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoogleProfileServiceImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val oneTapClient: SignInClient,
    private val signInClient: GoogleSignInClient,
) : GoogleProfileService {
    override val displayName = auth.currentUser?.displayName.toString()
    override val photoUrl = auth.currentUser?.photoUrl.toString()

    override suspend fun signOut(): SignOutResponse {
        return try {
            oneTapClient.signOut().await()
            auth.signOut()
            GoogleResponse.Success(data = true)
        } catch (e: ApiException) {
            GoogleResponse.Failure(e)
        }
    }

    override suspend fun revokeAccess(): RevokeAccessResponse {
        return try {
            auth.currentUser?.apply {
                signInClient.revokeAccess().await()
                oneTapClient.signOut().await()
                delete().await()
            }
            GoogleResponse.Success(data = true)
        } catch (e: ApiException) {
            GoogleResponse.Failure(e)
        }
    }
}
