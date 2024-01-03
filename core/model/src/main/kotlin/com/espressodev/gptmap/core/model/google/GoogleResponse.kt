package com.espressodev.gptmap.core.model.google

sealed class GoogleResponse<out T> {
    data object Loading : GoogleResponse<Nothing>()
    data class Success<out T>(
        val data: T?
    ) : GoogleResponse<T>()

    data class Failure(
        val e: Exception
    ) : GoogleResponse<Nothing>()
}

object GoogleConstants {
    const val SIGN_IN_REQUEST = "signInRequest"
    const val SIGN_UP_REQUEST = "signUpRequest"
}
