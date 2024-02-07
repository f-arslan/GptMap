package com.espressodev.gptmap.core.common

interface DataStoreService {
    suspend fun setUserFullName(fullName: String)
    suspend fun getUserFullName(): String

    suspend fun setImageUrl(imageUrl: String)
    suspend fun getImageUrl(): String
}
