package com.espressodev.gptmap.core

object Exceptions {
    class UserIdIsNullException : Exception("Firebase: User id is null")
    class EmailVerificationIsFalseException : Exception("Firebase: Email verification is false")
    class RealmUserIdNullException : Exception("Realm: User id is null")
}