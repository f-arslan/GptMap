package com.espressodev.gptmap.core.model

sealed class Response<out T> {
    data object Loading : Response<Nothing>()
    data class Success<out R>(val data: R) : Response<R>()
    data class Failure(val e: Exception) : Response<Nothing>()
}

sealed interface LoadingState {
    data object Idle : LoadingState
    data object Loading : LoadingState
}

