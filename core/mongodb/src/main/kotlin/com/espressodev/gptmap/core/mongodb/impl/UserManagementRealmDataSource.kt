package com.espressodev.gptmap.core.mongodb.impl

import com.espressodev.gptmap.core.model.di.Dispatcher
import com.espressodev.gptmap.core.model.di.GmDispatchers.IO
import com.espressodev.gptmap.core.model.realm.RealmUser
import com.espressodev.gptmap.core.mongodb.RealmDataSourceBase
import com.espressodev.gptmap.core.mongodb.UserManagementRealmRepository
import com.espressodev.gptmap.core.mongodb.module.RealmManager.realm
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserManagementRealmDataSource @Inject constructor(@Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher) :
    UserManagementRealmRepository, RealmDataSourceBase() {
    override suspend fun saveUser(realmUser: RealmUser): Result<Unit> =
        performRealmTransaction(ioDispatcher) {
            copyToRealm(
                realmUser.apply {
                    userId = realmUserId
                },
                updatePolicy = UpdatePolicy.ALL
            )
        }

    override suspend fun isUserInDatabase(): Result<Boolean> = runCatching {
        withContext(ioDispatcher) {
            realm.query<RealmUser>("userId == $0", realmUserId).first().find() != null
        }
    }

    override suspend fun deleteUser(): Result<Unit> = performRealmTransaction(ioDispatcher) {
        val userToDelete: RealmUser = query<RealmUser>("userId == $0", realmUserId)
            .find()
            .first()
        delete(userToDelete)
    }
}
