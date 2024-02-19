package com.espressodev.gptmap.core.datastore

interface DataStoreService {
    suspend fun setUserFullName(fullName: String)
    suspend fun getUserFullName(): String

    suspend fun setImageUrl(imageUrl: String)
    suspend fun getImageUrl(): String

    suspend fun setLatestImageIdForChat(imageId: String)
    suspend fun getLatestImageIdForChat(): String
    suspend fun clear()
}
