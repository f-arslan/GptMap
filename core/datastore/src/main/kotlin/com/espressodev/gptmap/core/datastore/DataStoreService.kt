package com.espressodev.gptmap.core.datastore

import kotlinx.coroutines.flow.Flow

interface DataStoreService {
    val userData: Flow<UserData>
    suspend fun setUserFullName(fullName: String)
    suspend fun getUserFullName(): Flow<String>
    suspend fun setLatestImageUrl(imageUrl: String)
    suspend fun getImageUrl(): Flow<String>
    suspend fun setLatestImageIdForChat(imageId: String)
    suspend fun getLatestImageIdForChat(): Flow<String>
    suspend fun clear()
}
