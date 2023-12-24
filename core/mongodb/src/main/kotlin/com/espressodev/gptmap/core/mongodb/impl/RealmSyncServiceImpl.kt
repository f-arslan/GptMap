package com.espressodev.gptmap.core.mongodb.impl

import android.util.Log
import com.espressodev.gptmap.core.model.realm.Hero
import com.espressodev.gptmap.core.model.realm.RealmLocation
import com.espressodev.gptmap.core.model.realm.RealmUser
import com.espressodev.gptmap.core.mongodb.RealmSyncService
import com.espressodev.gptmap.core.mongodb.module.RealmModule
import com.espressodev.gptmap.core.mongodb.module.RealmModule.realm
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.asFlow
import io.realm.kotlin.ext.query

class RealmSyncServiceImpl : RealmSyncService {

    override suspend fun saveUser(realmUser: RealmUser): Result<Boolean> = runCatching {
        realm.write {
            copyToRealm(
                realmUser.apply {
                    userId = RealmModule.realmUser.id
                }, updatePolicy = UpdatePolicy.ALL
            )
        }
        true
    }.onFailure {
        Log.e("RealmSyncServiceImpl", "addUser: failure $it")
        Result.failure<Throwable>(it)
    }

    override suspend fun saveLocation(realmLocation: RealmLocation): Result<Boolean> = runCatching {
        realm.write {
            copyToRealm(
                realmLocation.apply {
                    userId = RealmModule.realmUser.id
                }, updatePolicy = UpdatePolicy.ALL
            )
        }
        true
    }.onFailure {
        Log.e("RealmSyncServiceImpl", "addLocation: failure $it")
        Result.failure<Throwable>(it)
    }

    override suspend fun saveHero(): Result<Boolean> {
        return runCatching {
            realm.write {
                copyToRealm(
                   Hero().apply {
                       userId = RealmModule.realmUser.id
                       name = "Hero"
                   }, updatePolicy = UpdatePolicy.ALL
                )
            }
            true
        }.onFailure {
            Log.e("RealmSyncServiceImpl", "addHero: failure $it")
            Result.failure<Throwable>(it)
        }
    }

    override fun isUserInDatabase(userId: String): Boolean =
        realm.query<RealmUser>("userId == $0", userId).first().find() != null

}



