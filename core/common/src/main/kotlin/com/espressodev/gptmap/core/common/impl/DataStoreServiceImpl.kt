package com.espressodev.gptmap.core.common.impl

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
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

    companion object {
        private val USER_FULL_NAME = stringPreferencesKey("user_full_name")
        private val IMAGE_URL = stringPreferencesKey("image_url")
    }
}
