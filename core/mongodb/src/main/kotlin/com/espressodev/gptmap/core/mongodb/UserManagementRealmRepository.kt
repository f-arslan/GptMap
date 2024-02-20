package com.espressodev.gptmap.core.mongodb

import com.espressodev.gptmap.core.model.realm.RealmUser

interface UserManagementRealmRepository {
    suspend fun saveUser(realmUser: RealmUser): Result<Unit>
    suspend fun isUserInDatabase(): Result<Boolean>
    suspend fun deleteUser(): Result<Unit>
}
