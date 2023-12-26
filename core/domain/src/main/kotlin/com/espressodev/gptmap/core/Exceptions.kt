package com.espressodev.gptmap.core

object Exceptions {
    class FirebaseUserIdIsNullException : Exception()
    class FirebaseEmailVerificationIsFalseException : Exception()
}