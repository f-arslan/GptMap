package com.espressodev.gptmap.core.domain

import com.espressodev.gptmap.core.data.FirestoreService
import com.espressodev.gptmap.core.ext.runCatchingWithContext
import com.espressodev.gptmap.core.mongodb.UserManagementService
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class AddDatabaseIfUserIsNewUseCase @Inject constructor(
    private val firestoreService: FirestoreService,
    private val userManagementService: UserManagementService,
    private val ioDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(): Result<Unit> = runCatchingWithContext(ioDispatcher) {
        val isUserInRealmDb = userManagementService.isUserInDatabase().getOrThrow()
        if (isUserInRealmDb) return@runCatchingWithContext
        val realmUser = firestoreService.getUser().toRealmUser()
        userManagementService.saveUser(realmUser).getOrThrow()
    }
}
