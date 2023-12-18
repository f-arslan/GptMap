package com.espressodev.gptmap.core.domain

import com.espressodev.gptmap.core.Exceptions
import com.espressodev.gptmap.core.data.AccountService
import com.espressodev.gptmap.core.mongodb.RealmSyncService
import com.espressodev.gptmap.core.mongodb.module.RealmModule.realmUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AddDatabaseIfUserIsNewUseCase @Inject constructor(
    private val accountService: AccountService,
    private val realmSyncService: RealmSyncService
) {
    suspend operator fun invoke(): Result<Boolean> = withContext(Dispatchers.IO) {
        val isUserInRealmDb = realmSyncService.isUserInDatabase(realmUser.id)
        if (isUserInRealmDb) {
            return@withContext Result.success(value = true)
        }

        val user = accountService.currentUser ?: throw Exceptions.UserIdIsNullException()



        Result.success(value = true)
    }
}

