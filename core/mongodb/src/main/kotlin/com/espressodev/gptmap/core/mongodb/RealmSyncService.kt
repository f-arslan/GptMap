package com.espressodev.gptmap.core.mongodb

import com.espressodev.gptmap.core.model.realm.RealmUser

interface RealmSyncService {
    suspend fun addUser(user: RealmUser)
    fun pauseSync()
    fun resumeSync()
    fun close()
}
