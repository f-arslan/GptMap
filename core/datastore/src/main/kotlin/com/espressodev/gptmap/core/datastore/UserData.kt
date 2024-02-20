package com.espressodev.gptmap.core.datastore

data class UserData(
    val fullName: String = "",
    val latestImageUrlForChat: String = "",
    val latestImageIdForChat: String = ""
)
