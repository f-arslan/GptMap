package com.espressodev.gptmap.core.model

object Exceptions {
    class FirebaseUserIdIsNullException : Exception()
    class FirebaseUserIsNullException : Exception()
    class FirebaseEmailVerificationIsFalseException : Exception()
    class FirebaseDisplayNameNullException : Exception()
    class FirebaseEmailNullException : Exception()
    class FirebasePhotoUrlNullException : Exception()
    class RealmFailedToLoadFavouritesException : Exception()
    class RealmFailedToLoadImageAnalysesException : Exception()
    class RealmFailedToLoadImageAnalysisException : Exception()
}
