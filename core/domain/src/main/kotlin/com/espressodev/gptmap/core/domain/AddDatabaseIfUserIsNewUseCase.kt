package com.espressodev.gptmap.core.domain

import com.espressodev.gptmap.core.data.AccountService
import com.espressodev.gptmap.core.data.FirestoreService
import com.espressodev.gptmap.core.mongodb.RealmSyncService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AddDatabaseIfUserIsNewUseCase @Inject constructor(
    private val accountService: AccountService,
    private val firestoreService: FirestoreService,
    private val realmSyncService: RealmSyncService
) {
    suspend operator fun invoke(): Result<Boolean> = withContext(Dispatchers.IO) {
        val isUserInRealmDb = async { realmSyncService.isUserInDatabase() }.await()
        if (isUserInRealmDb) {
            return@withContext Result.success(value = true)
        }
        val realmUser = firestoreService.getUser(accountService.currentUser.uid).toRealmUser()
        realmSyncService.saveUser(realmUser).getOrThrow()
        Result.success(value = true)
    }
}
