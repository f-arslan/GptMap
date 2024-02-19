package com.espressodev.gptmap.core.mongodb.impl

import com.espressodev.gptmap.core.model.realm.RealmUser
import com.espressodev.gptmap.core.mongodb.RealmDataSourceBase
import com.espressodev.gptmap.core.mongodb.UserManagementDataSource
import com.espressodev.gptmap.core.mongodb.module.RealmManager.realm
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query

class UserManagementDataSourceImpl : UserManagementDataSource, RealmDataSourceBase() {
    override suspend fun saveUser(realmUser: RealmUser): Result<Unit> = performRealmTransaction {
        copyToRealm(
            realmUser.apply {
                userId = realmUserId
            },
            updatePolicy = UpdatePolicy.ALL
        )
    }

    override fun isUserInDatabase(): Result<Boolean> = runCatching {
        realm.query<RealmUser>("userId == $0", realmUserId).first().find() != null
    }

    override suspend fun deleteUser(): Result<Unit> = performRealmTransaction {
        val userToDelete: RealmUser = query<RealmUser>("userId == $0", realmUserId)
            .find()
            .first()
        delete(userToDelete)
    }
}
