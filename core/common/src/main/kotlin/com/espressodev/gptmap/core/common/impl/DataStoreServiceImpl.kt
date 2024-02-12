package com.espressodev.gptmap.core.common.impl

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.espressodev.gptmap.core.common.DataStoreService
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class DataStoreServiceImpl @Inject constructor(
    private val userDataStorePreferences: DataStore<Preferences>
) : DataStoreService {
    override suspend fun setUserFullName(fullName: String) {
        userDataStorePreferences.edit { preferences ->
            preferences[USER_FULL_NAME] = fullName
        }
    }

    override suspend fun getUserFullName(): String {
        val preferences = userDataStorePreferences.data.first()
        return preferences[USER_FULL_NAME] ?: ""
    }

    override suspend fun setImageUrl(imageUrl: String) {
        userDataStorePreferences.edit { preferences ->
            preferences[IMAGE_URL] = imageUrl
        }
    }

    override suspend fun getImageUrl(): String {
        val preferences = userDataStorePreferences.data.first()
        return preferences[IMAGE_URL] ?: ""
    }

    override suspend fun setLatestImageIdForChat(imageId: String) {
        userDataStorePreferences.edit { preferences ->
            preferences[LATEST_IMAGE_ID_FOR_CHAT] = imageId
        }
    }

    override suspend fun getLatestImageIdForChat(): String {
        val preferences = userDataStorePreferences.data.first()
        return preferences[LATEST_IMAGE_ID_FOR_CHAT] ?: ""
    }

    override suspend fun clear() {
        userDataStorePreferences.edit { preferences ->
            preferences.clear()
        }
    }

    private companion object {
        val USER_FULL_NAME = stringPreferencesKey("user_full_name")
        val IMAGE_URL = stringPreferencesKey("image_url")
        val LATEST_IMAGE_ID_FOR_CHAT = stringPreferencesKey("latest_image_id_for_chat")
    }
}
