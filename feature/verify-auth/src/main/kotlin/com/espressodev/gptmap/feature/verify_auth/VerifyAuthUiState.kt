package com.espressodev.gptmap.feature.verify_auth

import com.espressodev.gptmap.core.model.Response
import com.espressodev.gptmap.core.model.firebase.User

data class VerifyAuthUiState(
    val user: Response<User> = Response.Loading,
    val password: String = "",
    val isLoading: Boolean = false
)
