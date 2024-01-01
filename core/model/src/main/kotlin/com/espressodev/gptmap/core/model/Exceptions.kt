package com.espressodev.gptmap.core.model

object Exceptions {
    class FirebaseUserIdIsNullException : Exception()
    class FirebaseEmailVerificationIsFalseException : Exception()
    class RealmFavouriteNotFoundException: Exception()
}