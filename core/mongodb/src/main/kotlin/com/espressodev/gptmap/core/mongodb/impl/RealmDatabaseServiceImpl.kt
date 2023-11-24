package com.espressodev.gptmap.core.mongodb.impl

import com.espressodev.gptmap.core.model.Response
import com.espressodev.gptmap.core.model.realm.RealmUser
import com.espressodev.gptmap.core.mongodb.RealmDatabaseService
import com.espressodev.gptmap.core.mongodb.SaveUserToDatabaseResponse
import io.realm.kotlin.Realm
import io.realm.kotlin.mongodb.User

class RealmDatabaseServiceImpl(private val user: User?, private val realm: Realm?) :
    RealmDatabaseService {
    override suspend fun saveUserToDatabase(realmUser: RealmUser): SaveUserToDatabaseResponse {
        if (isRealmNotInitialized()) return Response.Failure(Exception("Realm not initialized"))
        realm!!.write {
            copyToRealm(realmUser.apply { userId = user!!.id })
        }.also {
            println(it)
        }
        return Response.Success(true)
    }

    private fun isRealmNotInitialized(): Boolean {
        return (user == null || realm == null)
    }
}