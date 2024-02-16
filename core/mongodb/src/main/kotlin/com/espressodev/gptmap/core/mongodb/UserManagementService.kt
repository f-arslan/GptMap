package com.espressodev.gptmap.core.mongodb

import com.espressodev.gptmap.core.model.realm.RealmUser

interface UserManagementService {
    suspend fun saveUser(realmUser: RealmUser): Result<Unit>
    fun isUserInDatabase(): Result<Boolean>
    suspend fun deleteUser(): Result<Unit>
}
