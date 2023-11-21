package com.espressodev.gptmap.core.google_auth

import com.espressodev.gptmap.core.model.google.GoogleResponse

typealias SignOutResponse = GoogleResponse<Boolean>
typealias RevokeAccessResponse = GoogleResponse<Boolean>


interface GoogleProfileService {
    val displayName: String
    val photoUrl: String

    suspend fun signOut(): SignOutResponse
    suspend fun revokeAccess(): RevokeAccessResponse
}