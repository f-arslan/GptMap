package com.espressodev.gptmap.core.datastore.impl

import androidx.datastore.core.DataStore
import com.espressodev.gptmap.core.datastore.DataStoreService
import com.espressodev.gptmap.core.datastore.UserData
import com.espressodev.gptmap.core.datastore.UserPreferences
import com.espressodev.gptmap.core.datastore.copy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DataStoreServiceImpl @Inject constructor(
    private val userPreferences: DataStore<UserPreferences>
) : DataStoreService {

    override val userData: Flow<UserData> = userPreferences.data.map {
        UserData(
            fullName = it.fullName,
            latestImageIdForChat = it.latestImageIdForChat,
            latestImageUrlForChat = it.latestImageUrlForChat
        )
    }

    override suspend fun setUserFullName(fullName: String) {
        userPreferences.updateData {
            it.copy {
                this.fullName = fullName
            }
        }
    }

    override suspend fun getUserFullName(): Flow<String> =
        userPreferences.data.map { it.fullName }

    override suspend fun setLatestImageUrl(imageUrl: String) {
        userPreferences.updateData {
            it.copy {
                latestImageUrlForChat = imageUrl
            }
        }
    }

    override suspend fun getImageUrl(): Flow<String> =
        userPreferences.data.map { it.latestImageUrlForChat }

    override suspend fun setLatestImageIdForChat(imageId: String) {
        userPreferences.updateData {
            it.copy {
                latestImageIdForChat = imageId
            }
        }
    }

    override suspend fun getLatestImageIdForChat(): Flow<String> =
        userPreferences.data.map { it.latestImageIdForChat }

    override suspend fun clear() {
        userPreferences.updateData {
            it.copy {
                fullName = ""
                latestImageUrlForChat = ""
                latestImageIdForChat = ""
            }
        }
    }
}
