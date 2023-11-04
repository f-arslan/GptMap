package com.espressodev.gptmap.core.model.google

import com.google.android.gms.maps.model.LatLng


sealed class GoogleResponse<out T> {
    data object Loading : GoogleResponse<Nothing>()
    data class Success<out T>(
        val data: T?
    ) : GoogleResponse<T>()

    data class Failure(
        val e: Exception
    ) : GoogleResponse<Nothing>()
}

sealed interface GoogleLocationResponse {
    data object Loading : GoogleLocationResponse
    data class Success(val location: LatLng?) : GoogleLocationResponse
    data class RevokedPermissions(val locationError: GoogleLocationError) : GoogleLocationResponse
}

sealed interface GoogleLocationError {
    data object NoPermissions : GoogleLocationError
    data object NoGps : GoogleLocationError
    data object NoConnection : GoogleLocationError
}

object GoogleConstants {
    const val SIGN_IN_REQUEST = "signInRequest"
    const val SIGN_UP_REQUEST = "signUpRequest"
}