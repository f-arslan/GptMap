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
    class GpsNotEnabledException : Exception("Please enable GPS")
    class LocationNullException : Exception("Location is not available")
    class LocationNullThrowable : Throwable("Location is not available")
    class ResponseTextNotFoundException : Exception()
    class RealmUserNotLoggedInException : Exception()
    class FirestoreUserNotExistsException : Exception()
    class FailedToCreateDirectoryException : IllegalStateException("Failed to create directory")

    class FailedToGetDirectoryException : IllegalStateException("Failed to get directory")
    class FailedToReadBitmapFromExternalStorageException :
        IllegalStateException("Failed to read bitmap from external storage")
}
