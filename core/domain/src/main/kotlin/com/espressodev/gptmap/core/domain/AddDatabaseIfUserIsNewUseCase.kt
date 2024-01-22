package com.espressodev.gptmap.core.domain

import com.espressodev.gptmap.core.data.FirestoreService
import com.espressodev.gptmap.core.mongodb.RealmSyncService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AddDatabaseIfUserIsNewUseCase @Inject constructor(
    private val firestoreService: FirestoreService,
    private val realmSyncService: RealmSyncService
) {
    suspend operator fun invoke(): Result<Boolean> = withContext(Dispatchers.IO) {
        runCatching {
            val isUserInRealmDb = realmSyncService.isUserInDatabase().getOrThrow()
            if (isUserInRealmDb) return@runCatching true
            val realmUser = firestoreService.getUser().toRealmUser()
            realmSyncService.saveUser(realmUser).getOrThrow()
            true
        }
    }
}
